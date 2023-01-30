package net.items.store.skywars.listener.entity;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.skywars.stats.SkyWarsStats;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if (entityDamageByEntityEvent.getEntity() instanceof Player
                && entityDamageByEntityEvent.getDamager() instanceof Player){
            IStatsManager statsManager = MiniGame.get(StatsManager.class);
            SkyWarsStats skyWarsStats = statsManager.loadStats(entityDamageByEntityEvent.getEntity().getUniqueId());
            skyWarsStats.setLastHitFrom(entityDamageByEntityEvent.getDamager().getUniqueId());
            skyWarsStats.setLastHitFromTime(System.currentTimeMillis());
        }
    }
}
