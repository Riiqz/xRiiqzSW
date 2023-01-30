package net.items.store.skywars.loot;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.location.ILocationManager;
import net.items.store.minigames.api.location.LocationState;
import net.items.store.minigames.api.map.GameMap;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.core.map.MapManager;
import net.items.store.minigames.core.map.location.LocationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;

import java.util.List;

public class SkyWarsLootManager {

    public static void prepareLoot(){
        prepareChests(ChestType.NORMAL);
        prepareChests(ChestType.MIDDLE);
    }

    private static void prepareChests(ChestType chestType){
        IMapManager mapManager = MiniGame.get(MapManager.class);
        GameMap gameMap = mapManager.getCurrentMap();
        ILocationManager locationManager = MiniGame.get(LocationManager.class);
        String chestIdentifier = locationManager
                .getReplacedIdentifier(chestType.getLocationIdentifier(), LocationState.COUNT, gameMap.getMapName());

        if (gameMap.getLocationList().containsKey(chestIdentifier)){
            List<Location> chestLocations = gameMap.getLocationList().get(chestIdentifier);

            Bukkit.getScheduler().callSyncMethod(MiniGame.getJavaPlugin(), () ->{
                for (Location location : chestLocations){
                    if (location.getBlock().getType() == Material.CHEST
                            || location.getBlock().getType() == Material.TRAPPED_CHEST){
                        Chest chest = (Chest) location.getBlock().getState();

                        LootManager.prepareChest(chest.getInventory(), chestType);
                    }
                }
                return null;
            });
        }
    }
}
