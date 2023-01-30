package net.items.store.skywars.listener.countdown;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.countdown.ICountdownManager;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.core.countdown.CountdownManager;
import net.items.store.minigames.core.event.CountdownCountEvent;
import net.items.store.minigames.core.map.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkyWarsWaitingCountListener implements Listener {

    private int freezeCount = 5;

    @EventHandler
    public void onCountdownCount(CountdownCountEvent countdownCountEvent){
        if (countdownCountEvent.getCountdownTask().getGameState() == GameState.WAITING){
            if (freezeCount > 0){
                String color = freezeCount == 5 ? "§c" : freezeCount <= 4 && freezeCount >= 2 ? "§e" : freezeCount == 1 ? "§a" : "";
                IMapManager mapManager = MiniGame.get(MapManager.class);

                for (Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1, 1);
                    player.sendTitle(color + freezeCount, "§f" + mapManager.getCurrentMap().getMapName(), 5, 20, 5);
                }

                freezeCount -= 1;
            } else {
                ICountdownManager countdownManager = MiniGame.get(CountdownManager.class);
                AbstractCountdownTask abstractCountdownTask = countdownManager.getCountdown(GameState.WAITING);

                if (abstractCountdownTask.isWait() == true){
                    abstractCountdownTask.setWait(false);

                    for (Player player : Bukkit.getOnlinePlayers()){
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }
                }
            }
        }
    }
}
