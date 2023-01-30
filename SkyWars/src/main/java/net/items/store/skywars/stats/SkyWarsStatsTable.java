package net.items.store.skywars.stats;

import com.google.common.collect.Lists;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.sql.IMySQL;
import net.items.store.minigames.api.sql.SQLDataColumn;
import net.items.store.minigames.api.sql.SQLDataType;
import net.items.store.minigames.core.mysql.AbstractSQLDataTable;
import net.items.store.minigames.core.mysql.MySQL;

import java.sql.ResultSet;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkyWarsStatsTable extends AbstractSQLDataTable {

    public SkyWarsStatsTable(IMySQL mySQL) {
        super(mySQL, "SkyWarsStats");
    }

    @Override
    protected void registerTableColumns() {
        this.tableColumnsList.add(new SQLDataColumn(COL_UNIQUEID, SQLDataType.VARCHAR, 100, true));
        this.tableColumnsList.add(new SQLDataColumn(COL_KILLS, SQLDataType.LONG));
        this.tableColumnsList.add(new SQLDataColumn(COL_DEATHS, SQLDataType.LONG));
        this.tableColumnsList.add(new SQLDataColumn(COL_POINTS, SQLDataType.LONG));
        this.tableColumnsList.add(new SQLDataColumn(COL_GAMES_WON, SQLDataType.LONG));
        this.tableColumnsList.add(new SQLDataColumn(COL_GAMES_LOST, SQLDataType.LONG));
        this.tableColumnsList.add(new SQLDataColumn(COL_GAMES_PLAYED, SQLDataType.LONG));
        this.tableColumnsList.add(new SQLDataColumn(COL_OPENED_CHESTS, SQLDataType.LONG));
    }

    public void createPlayer(UUID uniqueID){
        MiniGame.getExecutorService().submit(() -> {
            ResultSet resultSet = findPlayerResultSet(COL_UNIQUEID + "= '" + uniqueID.toString() + "'");

            try {
                if (resultSet == null || resultSet.next() == false) {
                    executeInsert(uniqueID, 0, 0, 0, 0, 0, 0, 0);
                }
            } catch (Exception exception){
                System.out.println(exception.getMessage());
            } finally {
                this.mySQL.closeResultSet(resultSet);
            }
        });
    }

    public int loadRank(UUID uniqueID) {
        int ranking = 0;

        if (mySQL.isMySQLConnected()){
            ResultSet resultSet = findPlayerResultSet
                    (
                            "",
                            COL_POINTS + " DESC"
                    );

            try {
                while (resultSet != null && resultSet.next() && resultSet.wasNull() == false){
                    ranking += 1;

                    if (resultSet.getString(COL_UNIQUEID).equalsIgnoreCase(uniqueID.toString())){
                        break;
                    }
                }
            } catch (Exception exception){
                System.out.println(exception.getMessage());
            } finally {
                mySQL.closeResultSet(resultSet);
            }
        }

        return ranking;
    }

    public List<Map.Entry<UUID, Integer>> loadTopTen() {
        List<Map.Entry<UUID, Integer>> entryList = Lists.newArrayList();

        if (mySQL.isMySQLConnected()){
            ResultSet resultSet = findPlayerResultSet
                    (
                            "",
                            COL_POINTS + " DESC LIMIT 10;"
                    );

            try {
                int ranking = 0;

                while (resultSet != null && resultSet.next() && resultSet.wasNull() == false){
                    ranking += 1;

                    UUID uuid = UUID.fromString(resultSet.getString(COL_UNIQUEID));

                    entryList.add(new AbstractMap.SimpleEntry<>(uuid, ranking));
                }
            } catch (Exception exception){
                System.out.println(exception.getMessage());
            } finally {
                mySQL.closeResultSet(resultSet);
            }
        }

        return entryList;
    }

    /*public List<Map.Entry<UUID, Map<Object, Object>>> loadTopTen() {
        List<Map.Entry<UUID, Map<Object, Object>>> entryList = Lists.newArrayList();

        if (mySQL.isMySQLConnected()){
            ResultSet resultSet = findPlayerResultSet
                    (
                            "",
                            COL_POINTS + " DESC LIMIT 10;"
                    );

            try {
                int ranking = 0;

                while (resultSet != null && resultSet.next() && resultSet.wasNull() == false){
                    ranking += 1;

                    UUID uuid = UUID.fromString(resultSet.getString(COL_UNIQUEID));
                    long kills = resultSet.getLong(COL_KILLS);
                    long deaths = resultSet.getLong(COL_DEATHS);
                    long points = resultSet.getLong(COL_POINTS);
                    long gamesWon = resultSet.getLong(COL_GAMES_WON);
                    long gamesLost = resultSet.getLong(COL_GAMES_LOST);
                    long gamesPlayed = resultSet.getLong(COL_GAMES_PLAYED);
                    long openedChests = resultSet.getLong(COL_OPENED_CHESTS);

                    SkyWarsStats skyWarsStats = new SkyWarsStats();
                    skyWarsStats.setUniqueID(uuid);
                    skyWarsStats.setKills(kills);
                    skyWarsStats.setDeaths(deaths);
                    skyWarsStats.setPoints(points);
                    skyWarsStats.setGamesWon(gamesWon);
                    skyWarsStats.setGamesLost(gamesLost);
                    skyWarsStats.setGamesPlayed(gamesPlayed);
                    skyWarsStats.setOpenedChests(openedChests);

                    entryList.add(new AbstractMap.SimpleEntry<>(uuid, skyWarsStats.getStatsObjectMap(ranking)));
                }
            } catch (Exception exception){
                System.out.println(exception.getMessage());
            } finally {
                mySQL.closeResultSet(resultSet);
            }
        }

        return entryList;
    }*/

    public void updatePlayerStats(SkyWarsStats skyWarsStats){
        MySQL mySQL = MiniGame.get(MySQL.class);

        if(mySQL.isMySQLConnected()){
            MiniGame.getExecutorService().submit(() -> {
                String statement = "UPDATE " + getTableName() + " SET " +
                        COL_KILLS + "= '" + skyWarsStats.getKills() + "', " +
                        COL_DEATHS + "= '" + skyWarsStats.getDeaths() + "', " +
                        COL_POINTS + "= '" + skyWarsStats.getPoints() + "', " +
                        COL_GAMES_WON + "= '" + skyWarsStats.getGamesWon() + "', " +
                        COL_GAMES_LOST + "= '" + skyWarsStats.getGamesLost() + "', " +
                        COL_GAMES_PLAYED + "= '" + skyWarsStats.getGamesPlayed() + "', " +
                        COL_OPENED_CHESTS + "= '" + skyWarsStats.getOpenedChests() + "' WHERE " +
                        COL_UNIQUEID + "= '" + skyWarsStats.getUniqueID() + "';";

                mySQL.executeStatement(statement);
            });
        }
    }

    public SkyWarsStats getPlayerStats(UUID uniqueID){
        MySQL mySQL = MiniGame.get(MySQL.class);

        if(mySQL.isMySQLConnected()){
            ResultSet resultSet = findPlayerResultSet(COL_UNIQUEID + "= '" + uniqueID.toString() + "'");
            SkyWarsStats skyWarsStats = null;

            try {
                if(resultSet != null && resultSet.next() && !resultSet.wasNull()){
                    long kills = resultSet.getLong(COL_KILLS);
                    long deaths = resultSet.getLong(COL_DEATHS);
                    long points = resultSet.getLong(COL_POINTS);
                    long gamesWon = resultSet.getLong(COL_GAMES_WON);
                    long gamesLost = resultSet.getLong(COL_GAMES_LOST);
                    long gamesPlayed = resultSet.getLong(COL_GAMES_PLAYED);
                    long openedChests = resultSet.getLong(COL_OPENED_CHESTS);

                    skyWarsStats = new SkyWarsStats();
                    skyWarsStats.setUniqueID(uniqueID);
                    skyWarsStats.setKills(kills);
                    skyWarsStats.setDeaths(deaths);
                    skyWarsStats.setPoints(points);
                    skyWarsStats.setGamesWon(gamesWon);
                    skyWarsStats.setGamesLost(gamesLost);
                    skyWarsStats.setGamesPlayed(gamesPlayed);
                    skyWarsStats.setOpenedChests(openedChests);
                }
            } catch (Exception exception){
                exception.printStackTrace();
            } finally {
                mySQL.closeResultSet(resultSet);
            }
            return skyWarsStats;
        }

        return null;
    }

    private final String COL_UNIQUEID = "UniqueID";
    private final String COL_KILLS = "Kills";
    private final String COL_DEATHS = "Deaths";
    private final String COL_POINTS = "Points";
    private final String COL_GAMES_WON = "GamesWon";
    private final String COL_GAMES_LOST = "GamesLost";
    private final String COL_GAMES_PLAYED = "GamesPlayed";
    private final String COL_OPENED_CHESTS = "OpenedChests";

}
