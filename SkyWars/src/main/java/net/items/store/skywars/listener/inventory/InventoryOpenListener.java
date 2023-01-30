package net.items.store.skywars.listener.inventory;

import net.items.store.minigames.api.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryOpenListener implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent){
        if (inventoryOpenEvent.getInventory().getType() == InventoryType.ENCHANTING){
            inventoryOpenEvent.getInventory().setItem(1, ItemBuilder.modify()
                    .setMaterial(Material.LAPIS_LAZULI).setAmount(64).buildItem());
        }
    }
}
