package de.riiqz.xnme.minigames.core.mysql;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.sql.IMySQL;
import net.items.store.minigames.api.sql.ISQLDataTable;
import net.items.store.minigames.api.sql.SQLDataColumn;
import net.items.store.minigames.api.sql.SQLDataType;

import javax.sound.midi.SysexMessage;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractSQLDataTable implements ISQLDataTable {

    @Getter
    private String tableName;

    protected IMySQL mySQL;

    protected List<SQLDataColumn> tableColumnsList;

    public AbstractSQLDataTable(IMySQL mySQL, String tableName){
        this.mySQL = mySQL;
        this.tableName = tableName;
        this.tableColumnsList = Lists.newArrayList();

        registerTableColumns();
        createTable();
    }

    private void createTable(){
        if (this.mySQL.isMySQLConnected()) {
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.tableName + " (");
            int current = 0;

            for (SQLDataColumn sqlDataColumn : this.tableColumnsList){
                if(current > 0){
                    stringBuilder.append(", ");
                }
                stringBuilder.append(sqlDataColumn.getName());
                stringBuilder.append(" ");
                stringBuilder.append(sqlDataColumn.getSqlDataType().getSqlName());

                if(sqlDataColumn.getSqlDataType() == SQLDataType.VARCHAR){
                    stringBuilder.append("(" + sqlDataColumn.getLength() + ")");
                } else {
                    if(sqlDataColumn.isCanBeNull() == false) {
                        stringBuilder.append(" NOT NULL");
                    }
                    if(sqlDataColumn.isAutoIncrement()) {
                        stringBuilder.append(" AUTO_INCREMENT");
                    }
                    if(sqlDataColumn.isUnique()){
                        stringBuilder.append(" UNIQUE");
                    }
                }
                current++;
            }
            stringBuilder.append(")");
            this.mySQL.executeStatement(stringBuilder.toString());
        }
    }

    protected void executeInsert(Object... objects){
        if (this.mySQL.isMySQLConnected()) {
            StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + this.tableName + " (");
            int current = 0;

            for (SQLDataColumn sqlDataColumn : this.tableColumnsList) {
                if (sqlDataColumn.isAutoIncrement() == false){
                    if(current > 0){
                        stringBuilder.append(", ");
                    }

                    stringBuilder.append(sqlDataColumn.getName());
                    current++;
                }
            }
            stringBuilder.append(") VALUES ('");
            stringBuilder.append(String.join("','",
                    Lists.newArrayList(objects).stream().map(x -> x.toString()).collect(Collectors.toList())));
            stringBuilder.append("');");

            this.mySQL.executeStatement(stringBuilder.toString());
        }
    }

    protected ResultSet findPlayerResultSet(String where){
        return findPlayerResultSet(where, "");
    }

    protected ResultSet findPlayerResultSet(String where, String orderBy){
        ResultSet resultSet = null;

        try {
            String sqlString = "SELECT * FROM " + this.tableName;

            if (where.equalsIgnoreCase("") == false){
                sqlString = sqlString + " WHERE " + where + "";
            }
            if (orderBy.equalsIgnoreCase("") == false){
                sqlString = sqlString + " ORDER BY " + orderBy + "";
            }

            resultSet = this.mySQL.executeQuery(sqlString);
        } catch (Exception exception){
            exception.printStackTrace();
        }

        return resultSet;
    }

    protected abstract void registerTableColumns();


}
