package net.items.store.skywars.stats;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.minigames.core.mysql.MySQL;
import org.bukkit.Bukkit;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatsManager implements IStatsManager {

    private Map<UUID, SkyWarsStats> cachedStatsMap;
    private Map<UUID, Map.Entry<Long, Integer>> topStatsMap;
    //private Map.Entry<Long, List<Map.Entry<UUID, Map<Object, Object>>>> topTenStatsCache;
    private Map.Entry<Long, List<Map.Entry<UUID, Integer>>> topTenStatsCache;

    public StatsManager(){
        this.cachedStatsMap = Maps.newHashMap();
        this.topTenStatsCache = null;
        this.topStatsMap = Maps.newHashMap();
    }

    @Override
    public int loadRank(UUID uniqueID) {
        SkyWarsStatsTable skyWarsStatsTable = MiniGame.get(SkyWarsStatsTable.class);

        if (topStatsMap.containsKey(uniqueID)){
            Map.Entry<Long, Integer> entry = topStatsMap.get(uniqueID);

            if (System.currentTimeMillis() - entry.getKey() < 30000){
                return entry.getValue();
            }
        }

        int ranking = skyWarsStatsTable.loadRank(uniqueID);
        topStatsMap.put(uniqueID, new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), ranking));
        return ranking;
    }

    /*@Override
    public List<Map.Entry<UUID, Map<Object, Object>>> loadTopTen() {
        SkyWarsStatsTable skyWarsStatsTable = MiniGame.get(SkyWarsStatsTable.class);

        if (topTenStatsCache != null && (System.currentTimeMillis() - topTenStatsCache.getKey()) < 60000){
            return topTenStatsCache.getValue();
        }

        List<Map.Entry<UUID, Map<Object, Object>>> topTen = skyWarsStatsTable.loadTopTen();
        topTenStatsCache = new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), topTen);
        return topTen;
    }*/

    @Override
    public List<Map.Entry<UUID, Integer>> loadTopTen() {
        SkyWarsStatsTable skyWarsStatsTable = MiniGame.get(SkyWarsStatsTable.class);

        if (topTenStatsCache != null && (System.currentTimeMillis() - topTenStatsCache.getKey()) < 60000){
            return topTenStatsCache.getValue();
        }

        //List<Map.Entry<UUID, Map<Object, Object>>> topTen = skyWarsStatsTable.loadTopTen();
        List<Map.Entry<UUID, Integer>> topTen = skyWarsStatsTable.loadTopTen();
        topTenStatsCache = new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), topTen);
        return topTen;
    }

    @Override
    public void createPlayer(UUID uniqueID) {
        SkyWarsStatsTable skyWarsStatsTable = MiniGame.get(SkyWarsStatsTable.class);
        skyWarsStatsTable.createPlayer(uniqueID);
    }

    @Override
    public SkyWarsStats loadStats(UUID uniqueID) {
        if(this.cachedStatsMap.containsKey(uniqueID)){
            return this.cachedStatsMap.get(uniqueID);
        }

        SkyWarsStatsTable skyWarsStatsTable = MiniGame.get(SkyWarsStatsTable.class);
        SkyWarsStats skyWarsStats = skyWarsStatsTable.getPlayerStats(uniqueID);
        if(skyWarsStats != null){
            this.cachedStatsMap.put(uniqueID, skyWarsStats);
        }
        return skyWarsStats;
    }

    @Override
    public <T> void saveStats(T t) {
        if(t instanceof SkyWarsStats == false){
            throw new NullPointerException();
        }

        MiniGame.getExecutorService().submit(() ->{
            SkyWarsStats skyWarsStats = (SkyWarsStats) t;
            SkyWarsStatsTable skyWarsStatsTable = MiniGame.get(SkyWarsStatsTable.class);
            skyWarsStatsTable.updatePlayerStats(skyWarsStats);
        });
    }

    @Override
    public boolean removeFromCachedStats(UUID uniqueID) {
        if(this.cachedStatsMap.containsKey(uniqueID)){
            this.cachedStatsMap.remove(uniqueID);
            return true;
        }
        return false;
    }

    @Override
    public List<SkyWarsStats> getCachedStats() {
        return this.cachedStatsMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public void clearCachedStats() {
        this.cachedStatsMap.clear();
    }

    @Override
    public Object getStatsObject(UUID uniqueID, String identifier) {
        SkyWarsStats skyWarsStats = loadStats(uniqueID);

        if(skyWarsStats == null){
            return null;
        }
        return skyWarsStats.getStatsObject(identifier);
    }

    @Override
    public List<String> getStatsStringList(UUID uniqueID) {
        SkyWarsStats skyWarsStats = loadStats(uniqueID);

        if(skyWarsStats == null){
            return null;
        }
        return skyWarsStats.getStatsStringList();
    }

    @Override
    public void registerDefault() {
        MySQL mySQL = MiniGame.get(MySQL.class);
        MiniGame.register(new SkyWarsStatsTable(mySQL));
    }
}
