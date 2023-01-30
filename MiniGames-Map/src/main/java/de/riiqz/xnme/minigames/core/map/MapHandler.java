package de.riiqz.xnme.minigames.core.map;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.map.GameMap;
import net.items.store.minigames.api.map.IMapHandler;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.map.MapBlockDirection;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.core.message.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.Optional;

public class MapHandler implements IMapHandler {

    @Override
    public void handleInventoryClickBlock(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();

        if (inventoryClickEvent.getCurrentItem() != null &&
                inventoryClickEvent.getClickedInventory() != player.getInventory()) {
            if(player.getOpenInventory().getTitle().equalsIgnoreCase("§eMap §8- §6Blöcke")) {
                String mapName = player.getOpenInventory().getItem(4).getItemMeta().clone()
                        .getDisplayName().replace("§6", "");
                IMapManager mapManager = MiniGame.get(MapManager.class);
                IMessageManager messageManager = MiniGame.get(MessageManager.class);

                if (inventoryClickEvent.getSlot() > 8 && inventoryClickEvent.getSlot() < 45) {
                    Optional<GameMap> optionalGameMap = mapManager.getMaps().stream()
                            .filter(x -> x.getMapName().equalsIgnoreCase(mapName))
                            .findFirst();
                    if (optionalGameMap.isPresent()) {
                        GameMap gameMap = optionalGameMap.get();
                        gameMap.setMapMaterial(inventoryClickEvent.getCurrentItem().getType());

                        Map<Object, Object> objectObjectMap = Maps.newHashMap();
                        objectObjectMap.put("{MAP}", mapName);

                        player.sendMessage(messageManager
                                .getMessage("UpdatedBlock", objectObjectMap));

                        mapManager.updateGameMaps();
                        mapManager.openMapLocationInventory(player, mapName);
                    }
                } else {
                    String currentSiteAsString = player.getOpenInventory().getItem(49)
                            .getItemMeta().getDisplayName().split(" ")[1];
                    String displayName = inventoryClickEvent.getCurrentItem().getItemMeta().clone()
                            .getDisplayName().replace("§e", "");
                    int currentSite = Integer.valueOf(currentSiteAsString.replace("§e", ""));

                    if(displayName.equalsIgnoreCase("Nächste Seite")){
                        mapManager.openMapBlockInventory(player, mapName, currentSite, MapBlockDirection.RIGHT);
                    } else if(displayName.equalsIgnoreCase("Vorherige Seite")){
                        mapManager.openMapBlockInventory(player, mapName, currentSite, MapBlockDirection.LEFT);
                    }
                }
            }
        }
    }
}
