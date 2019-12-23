package com.rtsw.openetl.testing.unit.common;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RT Software Studio
 */
public class Bootstrap {

    public static Table table() throws Exception {

        Table table = new Table();
        table.setName("Person");

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Id", "Integer", "java.lang.Integer"));
        columns.add(new Column("Firstname", "String", "java.lang.String"));
        columns.add(new Column("Lastname", "String", "java.lang.String"));
        columns.add(new Column("DateOfBirth", "Date", "java.util.Date"));
        columns.add(new Column("Alive", "Boolean", "java.lang.Boolean"));
        columns.add(new Column("Weight", "Double", "java.lang.Double"));
        columns.add(new Column("Height", "Double", "java.lang.Double"));


        table.setColumns(columns);

        return (table);

    }

    public static Row row() throws Exception {

        Row row = new Row();

        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add("John");
        values.add("Doe");
        values.add(new SimpleDateFormat("dd.MM.yyyy").parse("1.1.1980"));
        values.add(true);
        values.add(87.5);
        values.add(180.0);

        row.setValues(values);

        return (row);

    }

}
