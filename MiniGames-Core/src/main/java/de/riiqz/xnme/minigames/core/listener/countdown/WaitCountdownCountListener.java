package de.riiqz.xnme.minigames.core.listener.countdown;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.game.GameStateChangeReason;
import net.items.store.minigames.api.message.IMessageManager;
import de.riiqz.xnme.minigames.core.event.CountdownCountEvent;
import de.riiqz.xnme.minigames.core.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class WaitCountdownCountListener implements Listener {

    private GameState gameStateAfter;

    public WaitCountdownCountListener(GameState gameStateAfter){
        this.gameStateAfter = gameStateAfter;

        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("WaitingCountdownMessage", "{PREFIX}§7Die Schutzzeit endet in §e{COUNT} §7Sekunden.");
        messageManager.addMessage("WaitingCountdownMessageOne", "{PREFIX}§7Die Schutzzeit endet in §e{COUNT} §7Sekunde.");
        messageManager.addMessage("WaitingCountdownFinishMessage", "{PREFIX}§7Die Schutzzeit endet nun.");
    }

    @EventHandler
    public void onCountdownCount(CountdownCountEvent countdownCountEvent){
        if(countdownCountEvent.getCountdownTask().getGameState() == GameState.WAITING){
            AbstractCountdownTask abstractCountdownTask = countdownCountEvent.getCountdownTask();
            IMessageManager messageManager = MiniGame.get(MessageManager.class);

            if (abstractCountdownTask.isWait()){
                return;
            }

            int currentCount = abstractCountdownTask.getCount();

            Map<Object, Object> objectObjectMap = Maps.newHashMap();
            objectObjectMap.put("{COUNT}", currentCount);

            if(currentCount == 15 || currentCount == 10 || currentCount <= 5 && currentCount >= 2){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("WaitingCountdownMessage", objectObjectMap));
                }
            } else if(currentCount == 1){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("WaitingCountdownMessageOne", objectObjectMap));
                }
            } else if(currentCount == 0){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("WaitingCountdownFinishMessage", objectObjectMap));
                }

                MiniGame.get(AbstractGame.class).setGameState(this.gameStateAfter, GameStateChangeReason.NEXT_GAMESTATE);
                return;
            }

            currentCount -= 1;
            abstractCountdownTask.setCount(currentCount);
        }
    }
}
