package de.riiqz.xnme.minigames.core.listener.countdown;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.message.IMessageManager;
import de.riiqz.xnme.minigames.core.event.CountdownCountEvent;
import de.riiqz.xnme.minigames.core.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class RestartCountdownCountListener implements Listener {

    public RestartCountdownCountListener(){
        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("RestartCountdownMessage", "{PREFIX}§7Das Spiel stoppt in §e{COUNT} §7Sekunden.");
        messageManager.addMessage("RestartCountdownMessageOne", "{PREFIX}§7Das Spiel stoppt in §e{COUNT} §7Sekunde.");
    }

    @EventHandler
    public void onCountdownCount(CountdownCountEvent countdownCountEvent){
        if(countdownCountEvent.getCountdownTask().getGameState() == GameState.RESTART) {
            IMessageManager messageManager = MiniGame.get(MessageManager.class);
            AbstractCountdownTask abstractCountdownTask = countdownCountEvent.getCountdownTask();
            int currentCount = abstractCountdownTask.getCount();

            Map<Object, Object> objectObjectMap = Maps.newHashMap();
            objectObjectMap.put("{COUNT}", currentCount);

            if(currentCount == 15 || currentCount == 10 || currentCount <= 5 && currentCount >= 2){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("RestartCountdownMessage", objectObjectMap));
                }
            } else if(currentCount == 1){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(messageManager.getMessage("RestartCountdownMessageOne", objectObjectMap));
                }
            } else if(currentCount == 0){
                Bukkit.getServer().shutdown();
                return;
            }

            currentCount -= 1;
            abstractCountdownTask.setCount(currentCount);
        }
    }
}
