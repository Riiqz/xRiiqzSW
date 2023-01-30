package de.riiqz.xnme.minigames.core.map.listener;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.map.IMapManager;
import de.riiqz.xnme.minigames.core.map.MapManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        Player player = playerJoinEvent.getPlayer();
        IMapManager mapManager = MiniGame.get(MapManager.class);

        mapManager.teleportPlayerToLobby(player);
    }
}
