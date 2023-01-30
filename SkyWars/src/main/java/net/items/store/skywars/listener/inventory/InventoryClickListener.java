package net.items.store.skywars.listener.inventory;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent){
        GameState gameState = MiniGame.get(AbstractGame.class).getGameState();

        if (gameState == GameState.LOBBY || gameState == GameState.RESTART){
            inventoryClickEvent.setCancelled(true);
        }

        if (inventoryClickEvent.getClickedInventory() != null
                && inventoryClickEvent.getClickedInventory().getType() == InventoryType.ENCHANTING){
            if (inventoryClickEvent.getCurrentItem() != null
                    && inventoryClickEvent.getCurrentItem().getType() == Material.LAPIS_LAZULI){
                inventoryClickEvent.setCancelled(true);
            }
        }
    }
}
