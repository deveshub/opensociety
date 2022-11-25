package com.bits.opensociety.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FlatSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("flat_no", table, columnPrefix + "_flat_no"));
        columns.add(Column.aliased("residential_status", table, columnPrefix + "_residential_status"));
        columns.add(Column.aliased("flat_area", table, columnPrefix + "_flat_area"));

        columns.add(Column.aliased("flat_id", table, columnPrefix + "_flat_id"));
        return columns;
    }
}
