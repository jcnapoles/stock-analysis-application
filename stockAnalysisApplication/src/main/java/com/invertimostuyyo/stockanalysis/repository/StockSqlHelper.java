package com.invertimostuyyo.stockanalysis.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class StockSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("sector", table, columnPrefix + "_sector"));
        columns.add(Column.aliased("fundation", table, columnPrefix + "_fundation"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("icnome", table, columnPrefix + "_icnome"));
        columns.add(Column.aliased("expenses", table, columnPrefix + "_expenses"));
        columns.add(Column.aliased("capitalization", table, columnPrefix + "_capitalization"));
        columns.add(Column.aliased("employees", table, columnPrefix + "_employees"));

        return columns;
    }
}
