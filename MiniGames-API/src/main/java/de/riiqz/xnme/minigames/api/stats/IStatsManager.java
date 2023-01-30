package de.riiqz.xnme.minigames.api.stats;

import de.riiqz.xnme.minigames.api.IDefaultManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IStatsManager extends IDefaultManager {

    /**
     * Searches for stats in the Database.
     * If there are no stats in the database, they will be created.
     * @param uniqueID of the Player
     * @return an instance of the class PlayerStats
     */
    <T> T loadStats(UUID uniqueID);

    int loadRank(UUID uniqueID);

    //List<Map.Entry<UUID, Map<Object, Object>>> loadTopTen();
    List<Map.Entry<UUID, Integer>> loadTopTen();

    void createPlayer(UUID uniqueID);

    /**
     * Saves player stats to the Database
     * @param t
     */
    <T> void saveStats(T t);

    boolean removeFromCachedStats(UUID uniqueID);

    /**
     * @return All cached stats which have not yet been deleted.
     */
    <T> List<T> getCachedStats();

    /**
     * Clearing all cached stats
     */
    void clearCachedStats();

    Object getStatsObject(UUID uniqueID, String identifier);

    List<String> getStatsStringList(UUID uniqueID);

}
