package de.stondragonlp.syncstaticmapview.database.sql.builder;

/**
 * 資料表引擎類型枚舉
 */
public enum DatabaseEngine {
    MEMORY              ("'MEMORY'"),
    MyISAM              ("'MyISAM'"),
    InnoDB              ("'InnoDB'");



    private final String value;

    DatabaseEngine(final String v) {
        value = v;
    }

    public String part() {
        return value;
    }
}
