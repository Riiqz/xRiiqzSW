package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.countdown.ICountdownManager;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.core.countdown.CountdownManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private AbstractCountdownTask abstractCountdownTask;

    public PlayerMoveListener(){
        abstractCountdownTask = null;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent){
        if (abstractCountdownTask == null){
            ICountdownManager countdownManager = MiniGame.get(CountdownManager.class);
            abstractCountdownTask = countdownManager.getCountdown(GameState.WAITING);
        }

        if (abstractCountdownTask.isWait()){
            playerMoveEvent.setCancelled(true);

            Location locationTo = playerMoveEvent.getTo();
            Location locationFrom = playerMoveEvent.getFrom();

            if (locationFrom.getX() != locationTo.getX() || locationFrom.getZ() != locationTo.getZ()) {
                playerMoveEvent.getPlayer().teleport(locationFrom);
            }
        }
    }

}
