package de.riiqz.xnme.minigames.api.sql;

public enum SQLDataType {

    DOUBLE("double"),
    INT("int"),
    BOOLEAN("boolean"),
    LONG("long"),
    VARCHAR("varchar"),
    TEXT("text");

    private String sqlName;

    SQLDataType(String sqlName){
        this.sqlName = sqlName;
    }

    public String getSqlName() {
        return sqlName;
    }
}
