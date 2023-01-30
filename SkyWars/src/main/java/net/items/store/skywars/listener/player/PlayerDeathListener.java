package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.skywars.player.PlayerDeathManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent){
        playerDeathEvent.setDeathMessage(null);

        PlayerDeathManager playerDeathManager = MiniGame.get(PlayerDeathManager.class);
        playerDeathManager.playerDeath(playerDeathEvent.getEntity(), false);
    }
}
