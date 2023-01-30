package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameScoreboard;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.scoreboard.IScoreboardManager;
import net.items.store.skywars.scoreboard.SkyWarsScoreboardManager;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        Player player = playerJoinEvent.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        StatsManager statsManager = MiniGame.get(StatsManager.class);
        statsManager.createPlayer(player.getUniqueId());

        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);
        IScoreboardManager scoreboardManager = MiniGame.get(SkyWarsScoreboardManager.class);
        GameScoreboard gameScoreboard = scoreboardManager.findScoreboardByIdentifier(abstractGame.getGameState());

        if (gameScoreboard != null){
            scoreboardManager.sendScoreboardToPlayer(gameScoreboard, player, true);
        }

        if (abstractGame.getGameState() != GameState.LOBBY && abstractGame.getGameState() != GameState.RESTART){
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }
}
