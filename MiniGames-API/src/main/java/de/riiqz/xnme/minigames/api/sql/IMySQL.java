package de.riiqz.xnme.minigames.api.sql;

import java.sql.ResultSet;

public interface IMySQL {

    void executeStatement(String statement);

    boolean connectMySQL();

    boolean disconnectMySQL();

    boolean isMySQLConnected();

    ResultSet executeQuery(String query);

    boolean closeResultSet(ResultSet resultSet);

}
