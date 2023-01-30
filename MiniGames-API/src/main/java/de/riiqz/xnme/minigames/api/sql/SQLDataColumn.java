package de.riiqz.xnme.minigames.api.sql;

import lombok.Getter;

@Getter
public class SQLDataColumn {

    private String name;
    private SQLDataType sqlDataType;
    private int length;
    private boolean unique;
    private boolean autoIncrement;
    private boolean canBeNull;

    public SQLDataColumn(String name, SQLDataType sqlDataType){
        this(name, sqlDataType, 0, false, false, true);
    }

    public SQLDataColumn(String name, SQLDataType sqlDataType, int length){
        this(name, sqlDataType, length, false, false, true);
    }

    public SQLDataColumn(String name, SQLDataType sqlDataType, int length, boolean unique){
        this(name, sqlDataType, length, unique, false, true);
    }

    public SQLDataColumn(String name, SQLDataType sqlDataType, int length, boolean unique, boolean autoIncrement){
        this(name, sqlDataType, length, unique, autoIncrement, true);
    }

    public SQLDataColumn(String name, SQLDataType sqlDataType, int length, boolean unique, boolean autoIncrement, boolean canBeNull){
        this.name = name;
        this.sqlDataType = sqlDataType;
        this.length = length;
        this.unique = unique;
        this.autoIncrement = autoIncrement;
        this.canBeNull = canBeNull;
    }
}
