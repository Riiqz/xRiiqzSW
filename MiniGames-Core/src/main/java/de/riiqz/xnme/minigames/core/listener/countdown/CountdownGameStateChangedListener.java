package de.riiqz.xnme.minigames.core.listener.countdown;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.countdown.ICountdownManager;
import net.items.store.minigames.api.game.GameScoreboard;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.api.scoreboard.IScoreboardManager;
import net.items.store.minigames.api.team.ITeamManager;
import de.riiqz.xnme.minigames.core.event.CountdownGameStateChangedEvent;
import de.riiqz.xnme.minigames.core.game.Game;
import de.riiqz.xnme.minigames.core.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CountdownGameStateChangedListener implements Listener {

    @EventHandler
    public void onCountdownGameStateChanged(CountdownGameStateChangedEvent countdownGameStateChangedEvent){
        IScoreboardManager scoreboardManager = MiniGame.get(IScoreboardManager.class);
        if (scoreboardManager != null){
            GameScoreboard gameScoreboard = scoreboardManager
                    .findScoreboardByIdentifier(countdownGameStateChangedEvent.getNewGameState());

            if (gameScoreboard != null){
                for (Player player : Bukkit.getOnlinePlayers()){
                    scoreboardManager.sendScoreboardToPlayer(gameScoreboard, player, true);
                }
            }
        }

        switch(countdownGameStateChangedEvent.getOldGameState()){
            case LOBBY:
                countdownGameStateChangedEvent.setWait(true);

                Game game = MiniGame.get(Game.class);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    game.addPlayerAlive(player);
                }

                MiniGame.getExecutorService().submit(() ->{
                    try {
                        IMapManager mapManager = MiniGame.get(IMapManager.class);
                        IMessageManager messageManager = MiniGame.get(MessageManager.class);

                        for (Player player : Bukkit.getOnlinePlayers()){
                            player.sendMessage(messageManager.getMessage("WaitingForTeleport"));
                        }

                        mapManager.loadMap(mapManager.getCurrentMap());

                        countdownGameStateChangedEvent.setWait(false);
                    } catch (Exception exception){
                        System.out.println(exception.getMessage());
                    }
                });
                break;
            default:
                break;
        }
    }
}
