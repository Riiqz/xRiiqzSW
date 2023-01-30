package de.riiqz.xnme.minigames.core.coin;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.sql.IMySQL;
import net.items.store.minigames.api.sql.SQLDataColumn;
import net.items.store.minigames.api.sql.SQLDataType;
import de.riiqz.xnme.minigames.core.mysql.AbstractSQLDataTable;

import java.sql.ResultSet;
import java.util.UUID;

public class CoinTable extends AbstractSQLDataTable {

    public CoinTable(IMySQL mySQL) {
        super(mySQL, "PlayerCoins");
    }

    @Override
    protected void registerTableColumns() {
        this.tableColumnsList.add(new SQLDataColumn("UniqueID", SQLDataType.VARCHAR, 100, true));
        this.tableColumnsList.add(new SQLDataColumn("Coins", SQLDataType.LONG));
    }

    public void createPlayer(UUID uniqueID){
        MiniGame.getExecutorService().submit(() -> {
            ResultSet resultSet = findPlayerResultSet("UniqueID = '" + uniqueID.toString() + "'");

            try {
                if (resultSet == null || resultSet.next() == false || resultSet.wasNull() == true) {
                    executeInsert(uniqueID, 0);
                }
            } catch (Exception exception){
                System.out.println(exception.getMessage());
            } finally {
                this.mySQL.closeResultSet(resultSet);
            }
        });
    }

    public void setPlayerCoins(UUID uniqueID, long coins){
        if(this.mySQL.isMySQLConnected()){
            MiniGame.getExecutorService().submit(() -> {
                String statement = "UPDATE PlayerCoins SET Coins= '" + coins + "' WHERE UniqueID= '" + uniqueID + "';";

                this.mySQL.executeStatement(statement);
            });
        }
    }

    public long getPlayerCoins(UUID uniqueID){
        if(this.mySQL.isMySQLConnected()){
            ResultSet resultSet = findPlayerResultSet("UniqueID = '" + uniqueID.toString() + "'");
            int coins = 0;

            try {
                if(resultSet != null && resultSet.next() && !resultSet.wasNull()){
                    coins = resultSet.getInt("Coins");
                }
                if(resultSet != null){
                    this.mySQL.closeResultSet(resultSet);
                }
            } catch (Exception exception){
                exception.printStackTrace();
            }
            return coins;
        }
        return 0;
    }
}
