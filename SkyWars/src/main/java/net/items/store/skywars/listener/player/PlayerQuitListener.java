package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.skywars.player.PlayerDeathManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent){
        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);
        Player player = playerQuitEvent.getPlayer();

        if (abstractGame.isPlayerAlive(player)){
            PlayerDeathManager playerDeathManager = MiniGame.get(PlayerDeathManager.class);
            playerDeathManager.playerDeath(player, true);

            dropItems(player.getLocation(), player.getInventory().getArmorContents());
            dropItems(player.getLocation(), player.getInventory().getExtraContents());
            dropItems(player.getLocation(), player.getInventory().getContents());
            dropItems(player.getLocation(), player.getInventory().getStorageContents());
        }
    }

    private void dropItems(Location location, ItemStack[] itemStacks){
        if (itemStacks != null){
            for (int i = 0; i < itemStacks.length; i++){
                ItemStack itemStack = itemStacks[i];

                if (itemStack != null && itemStack.getType() != Material.AIR){
                    location.getWorld().dropItem(location, itemStack);
                }
            }
        }
    }
}
