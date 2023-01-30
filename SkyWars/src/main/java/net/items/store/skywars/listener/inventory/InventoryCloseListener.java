package net.items.store.skywars.listener.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent){
        if (inventoryCloseEvent.getInventory().getType() == InventoryType.ENCHANTING){
            ItemStack itemStack = inventoryCloseEvent.getInventory().getItem(1);
            if (itemStack != null){
                inventoryCloseEvent.getInventory().remove(itemStack);
            }
        }
    }
}
