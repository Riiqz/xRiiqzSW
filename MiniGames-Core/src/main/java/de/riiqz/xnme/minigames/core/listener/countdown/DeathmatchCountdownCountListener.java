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

public class DeathmatchCountdownCountListener implements Listener {

    private GameState gameStateAfter;

    public DeathmatchCountdownCountListener(GameState gameStateAfter){
        this.gameStateAfter = gameStateAfter;

        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("DeathMatchCountdownMessageMinutes", "{PREFIX}§7Das DeathMatch endet in §e{COUNT} §7Minuten.");
        messageManager.addMessage("DeathMatchCountdownMessageMinute", "{PREFIX}§7Das DeathMatch endet in §e{COUNT} §7Minute.");
        messageManager.addMessage("DeathMatchCountdownMessage", "{PREFIX}§7Das DeathMatch endet in §e{COUNT} §7Sekunden.");
        messageManager.addMessage("DeathMatchCountdownMessageOne", "{PREFIX}§7Das DeathMatch endet in §e{COUNT} §7Sekunde.");
        messageManager.addMessage("DeathMatchCountdownFinishMessage", "{PREFIX}§7Das DeathMatch endet nun.");
    }

    @EventHandler
    public void onCountdownCount(CountdownCountEvent countdownCountEvent){
        if(countdownCountEvent.getCountdownTask().getGameState() == GameState.DEATHMATCH){
            AbstractCountdownTask abstractCountdownTask = countdownCountEvent.getCountdownTask();
            IMessageManager messageManager = MiniGame.get(MessageManager.class);
            int currentCount = abstractCountdownTask.getCount();

            Map<Object, Object> objectObjectMap = Maps.newHashMap();
            objectObjectMap.put("{COUNT}", currentCount);

            if(currentCount == 600 || currentCount == 300 || currentCount == 180 || currentCount == 120){
                objectObjectMap.put("{COUNT}", currentCount / 60);

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("DeathMatchCountdownMessageMinutes", objectObjectMap));
                }
            } else if(currentCount == 60) {
                objectObjectMap.put("{COUNT}", currentCount / 60);

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("DeathMatchCountdownMessageMinute", objectObjectMap));
                }
            } else if(currentCount == 30 || currentCount == 15 ||
                    currentCount == 10 || currentCount <= 5 && currentCount >= 2) {
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("DeathMatchCountdownMessage", objectObjectMap));
                }
            } else if(currentCount == 1){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("DeathMatchCountdownMessageOne", objectObjectMap));
                }
            } else if(currentCount == 0){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("DeathMatchCountdownFinishMessage", objectObjectMap));
                }

                MiniGame.get(AbstractGame.class).setGameState(this.gameStateAfter, GameStateChangeReason.NEXT_GAMESTATE);
                return;
            }

            currentCount -= 1;
            abstractCountdownTask.setCount(currentCount);
        }
    }
}
