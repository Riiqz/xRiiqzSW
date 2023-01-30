package net.items.store.skywars.player;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.coin.ICoinManager;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.minigames.core.coin.CoinManager;
import net.items.store.minigames.core.message.MessageManager;
import net.items.store.skywars.stats.SkyWarsStats;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Random;

public class PlayerDeathManager {

    public void playerDeath(Player entity, boolean causeLeave){
        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        IStatsManager statsManager = MiniGame.get(StatsManager.class);

        SkyWarsStats skyWarsStats = statsManager.loadStats(entity.getUniqueId());
        skyWarsStats.setDeaths(skyWarsStats.getDeaths() + 1);
        skyWarsStats.setGamesLost(skyWarsStats.getGamesLost() + 1);
        statsManager.saveStats(skyWarsStats);

        Map<Object, Object> objectObjectMap = Maps.newHashMap();
        objectObjectMap.put("{ENTITY}", entity.getName());

        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);
        abstractGame.removePlayerAlive(entity);

        if (entity.getKiller() != null || skyWarsStats.getLastHitFrom() != null
                && (System.currentTimeMillis() - skyWarsStats.getLastHitFromTime()) < 30000){
            Player killer;

            if (entity.getKiller() == null){
                killer = Bukkit.getPlayer(skyWarsStats.getLastHitFrom());
            } else {
                killer = entity.getKiller().getPlayer();
            }

            if (killer != null && killer.isOnline() == true){
                SkyWarsStats killerSkyWarsStats = statsManager.loadStats(killer.getUniqueId());

                if (killerSkyWarsStats != null){
                    killerSkyWarsStats.setKills(killerSkyWarsStats.getKills() + 1);
                    killerSkyWarsStats.setGamesKills(killerSkyWarsStats.getGamesKills() + 1);
                    killerSkyWarsStats.setPoints(skyWarsStats.getPoints() + 5);

                    Random random = new Random();
                    ICoinManager coinManager = MiniGame.get(CoinManager.class);
                    coinManager.addCoins(killer, random.nextInt(12, 20));

                    statsManager.saveStats(killerSkyWarsStats);
                }

                objectObjectMap.put("{KILLER}", killer.getName());

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                    onlinePlayer.sendMessage(messageManager.getMessage("PlayerKilled", objectObjectMap));
                }

                if (causeLeave == false){
                    Bukkit.getScheduler().callSyncMethod(MiniGame.getJavaPlugin(), () ->{
                        entity.spigot().respawn();
                        return null;
                    });
                }
                return;
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
            onlinePlayer.sendMessage(messageManager.getMessage("PlayerDied", objectObjectMap));
        }

        if (causeLeave == false){
            Bukkit.getScheduler().callSyncMethod(MiniGame.getJavaPlugin(), () ->{
                entity.spigot().respawn();
                return null;
            });
        }
    }

}
