package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.skywars.stats.SkyWarsStats;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.EnchantingTable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent){
        if (playerInteractEvent.getClickedBlock() != null) {
            if (playerInteractEvent.getClickedBlock().getType() == Material.CHEST
                    || playerInteractEvent.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                Chest chest = (Chest) playerInteractEvent.getClickedBlock().getState();

                if (chest.hasMetadata("Opened") == false) {
                    chest.setMetadata("Opened", new FixedMetadataValue(MiniGame.getJavaPlugin(), "True"));

                    MiniGame.getExecutorService().submit(() -> {
                        IStatsManager statsManager = MiniGame.get(StatsManager.class);

                        SkyWarsStats skyWarsStats = statsManager.loadStats(playerInteractEvent.getPlayer().getUniqueId());
                        skyWarsStats.setOpenedChests(skyWarsStats.getOpenedChests() + 1);

                        statsManager.saveStats(skyWarsStats);
                    });
                }
            }
        }
    }
}
