package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.location.ILocationManager;
import net.items.store.minigames.api.location.LocationState;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.core.map.MapManager;
import net.items.store.minigames.core.map.location.LocationManager;
import net.items.store.minigames.core.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent){
        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);

        Player player = playerRespawnEvent.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(messageManager.getMessage("TeamSpectator"));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
            if (abstractGame.isPlayerAlive(onlinePlayer)){
                onlinePlayer.hidePlayer(MiniGame.getJavaPlugin(), player);
            }
        }

        ILocationManager locationManager = MiniGame.get(LocationManager.class);
        IMapManager mapManager = MiniGame.get(MapManager.class);

        playerRespawnEvent.setRespawnLocation(locationManager.getLocation("Spectator",
                LocationState.NORMAL, mapManager.getCurrentMap().getMapName()));
    }

}
