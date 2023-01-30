package net.items.store.skywars.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.minigames.core.data.MapBuilder;
import net.items.store.skywars.SkyWars;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class SkyWarsStats {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private UUID uniqueID;
    private long kills;
    private long gamesKills;
    private long deaths;
    private long points;
    private long gamesWon;
    private long gamesLost;
    private long gamesPlayed;
    private long openedChests;
    private UUID lastHitFrom;
    private long lastHitFromTime;

    public SkyWarsStats(){
        this.lastHitFrom = null;
        this.lastHitFromTime = System.currentTimeMillis();
        this.gamesKills = 0;
    }

    public String getKd(){
        if(kills == 0){
            return "0.00";
        } else if(deaths == 0){
            return kills + ".00";
        } else {
            return decimalFormat.format((double) kills / (double) deaths);
        }
    }

    public List<String> getStatsStringList(){
        return Lists.newArrayList(
                "Rank",
                "Kills",
                "Deaths",
                "KD",
                "Points",
                "Gespielte Spiele",
                "Gewonnene Spiele",
                "Verlorene Spiele",
                "Geöffnete Chests"
        );
    }

    public Map<Object, Object> getStatsObjectMap(int rank){
        Map<Object, Object> objectObjectMap = Maps.newHashMap();

        for (String identifier : getStatsStringList()){
            switch (identifier){
                case "Rank":
                    objectObjectMap.put(identifier, rank);
                    break;
                case "Kills":
                    objectObjectMap.put(identifier, kills);
                    break;
                case "Deaths":
                    objectObjectMap.put(identifier, deaths);
                    break;
                case "KD":
                    objectObjectMap.put(identifier, getKd());
                    break;
                case "Points":
                    objectObjectMap.put(identifier, points);
                    break;
                case "Gespielte Spiele":
                    objectObjectMap.put(identifier, gamesPlayed);
                    break;
                case "Gewonnene Spiele":
                    objectObjectMap.put(identifier, gamesWon);
                    break;
                case "Verlorene Spiele":
                    objectObjectMap.put(identifier, gamesLost);
                    break;
                case "Geöffnete Chests":
                    objectObjectMap.put(identifier, openedChests);
                    break;
            }
        }

        return objectObjectMap;
    }

    public Object getStatsObject(String identifier){
        switch (identifier){
            case "Rank":
                IStatsManager statsManager = MiniGame.get(StatsManager.class);
                return statsManager.loadRank(uniqueID);
            case "Kills":
                return kills;
            case "Deaths":
                return deaths;
            case "KD":
                return getKd();
            case "Points":
                return points;
            case "Gespielte Spiele":
                return gamesPlayed;
            case "Gewonnene Spiele":
                return gamesWon;
            case "Verlorene Spiele":
                return gamesLost;
            case "Geöffnete Chests":
                return openedChests;
        }

        return "NULL";
    }
}
