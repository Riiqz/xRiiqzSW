package de.riiqz.xnme.minigames.core.listener.countdown;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.countdown.CountDirection;
import net.items.store.minigames.api.game.GameScoreboard;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.scoreboard.IScoreboardManager;
import de.riiqz.xnme.minigames.core.event.CountdownCountEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IngameCountdownCountListener implements Listener {

    @EventHandler
    public void onCountdownCount(CountdownCountEvent countdownCountEvent){
        if(countdownCountEvent.getCountdownTask().getGameState() == GameState.INGAME){
            AbstractCountdownTask abstractCountdownTask = countdownCountEvent.getCountdownTask();

            int currentCount = abstractCountdownTask.getCount();
            if (countdownCountEvent.getCountdownTask().getCountDirection() == CountDirection.UP){
                currentCount += 1;
            } else if (countdownCountEvent.getCountdownTask().getCountDirection() == CountDirection.DOWN){
                currentCount -= 1;
            }

            abstractCountdownTask.setCount(currentCount);

            IScoreboardManager scoreboardManager = MiniGame.get(IScoreboardManager.class);
            GameScoreboard gameScoreboard = scoreboardManager.findScoreboardByIdentifier(GameState.INGAME);

            if (gameScoreboard != null){
                for(Player player : Bukkit.getOnlinePlayers()){
                    scoreboardManager.updateScoreboardForPlayer(gameScoreboard, player);
                }
            }
        }
    }
}
