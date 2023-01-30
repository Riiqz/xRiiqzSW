package de.riiqz.xnme.minigames.core.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.message.IMessageManager;
import de.riiqz.xnme.minigames.core.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        playerJoinEvent.setJoinMessage(null);

        Player player = playerJoinEvent.getPlayer();
        player.setFoodLevel(20);
        player.setHealth(20);

        for (PotionEffect potionEffect : player.getActivePotionEffects()){
            player.removePotionEffect(potionEffect.getType());
        }

        IMapManager mapManager = MiniGame.get(IMapManager.class);
        mapManager.teleportPlayerToLobby(player);

        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);

        if (abstractGame.getGameState() != GameState.LOBBY && abstractGame.getGameState() != GameState.RESTART){
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(messageManager.getMessage("TeamSpectator"));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                if (abstractGame.isPlayerAlive(onlinePlayer)){
                    onlinePlayer.hidePlayer(MiniGame.getJavaPlugin(), player);
                }
            }
        } else {
            player.setGameMode(GameMode.SURVIVAL);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                onlinePlayer.showPlayer(MiniGame.getJavaPlugin(), player);
            }
        }
    }

}
