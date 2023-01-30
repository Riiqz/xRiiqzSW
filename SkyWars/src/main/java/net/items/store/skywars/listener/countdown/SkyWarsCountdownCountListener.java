package net.items.store.skywars.listener.countdown;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.coin.ICoinManager;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.game.GameStateChangeReason;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.minigames.api.team.GameTeam;
import net.items.store.minigames.api.team.ITeamManager;
import net.items.store.minigames.core.coin.CoinManager;
import net.items.store.minigames.core.event.CountdownCountEvent;
import net.items.store.minigames.core.game.Game;
import net.items.store.minigames.core.message.MessageManager;
import net.items.store.minigames.core.team.TeamManager;
import net.items.store.skywars.stats.SkyWarsStats;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SkyWarsCountdownCountListener implements Listener {

    @EventHandler
    public void onSkyWarsCountdownCount(CountdownCountEvent countdownCountEvent){
        if (countdownCountEvent.getCountdownTask().getGameState() != GameState.LOBBY
                && countdownCountEvent.getCountdownTask().getGameState() != GameState.RESTART){
            Game game = MiniGame.get(Game.class);
            ITeamManager teamManager = MiniGame.get(TeamManager.class);

            List<GameTeam> teams = teamManager.getTeams().stream()
                    .filter(x -> game.containsAlivePlayers(x.getTeamPlayers()).size() > 0)
                    .collect(Collectors.toList());

            if (teams.size() <= 1){
                if (game.getAlivePlayers().size() > 0) {
                    for (UUID uuid : teams.get(0).getTeamPlayers()) {
                        IStatsManager statsManager = MiniGame.get(StatsManager.class);
                        SkyWarsStats skyWarsStats = statsManager.loadStats(uuid);
                        skyWarsStats.setGamesWon(skyWarsStats.getGamesWon() + 1);
                        skyWarsStats.setPoints(skyWarsStats.getPoints() + 50);

                        ICoinManager coinManager = MiniGame.get(CoinManager.class);
                        coinManager.addCoins(Bukkit.getPlayer(uuid), 150);

                        statsManager.saveStats(skyWarsStats);
                    }

                    IMessageManager messageManager = MiniGame.get(MessageManager.class);
                    Map<Object, Object> objectObjectMap = Maps.newHashMap();
                    objectObjectMap.put("{TEAM_NAME}", teams.get(0).getTeamName());
                    objectObjectMap.put("{TEAM_PREFIX}", teams.get(0).getTeamPrefix());

                    for (Player player : Bukkit.getOnlinePlayers()){
                        player.sendTitle(messageManager.getMessage("WinTitle", objectObjectMap),
                                messageManager.getMessage("WinSubtitle"),
                                10, 200, 10);
                    }
                }

                game.setGameState(GameState.RESTART, GameStateChangeReason.PLAYERS_ALIVE);
            }
        }
    }
}
