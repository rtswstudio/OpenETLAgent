package com.rtsw.openetl.agent.transform;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.api.Transform;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author RT Software Studio
 */
public class DropColumnTransform implements Transform {

    private static final String ID = DropColumnTransform.class.getName();

    private Report report = new Report(ID);

    private String tablePattern;

    private String columnPattern;

    private Set<Integer> indexes;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        tablePattern = configuration.get("table_pattern", null);
        if (tablePattern == null) {
            throw new Exception("missing required parameter 'table_pattern'");
        }

        // required
        columnPattern = configuration.get("column_pattern", null);
        if (columnPattern == null) {
            throw new Exception("missing required parameter 'column_pattern'");
        }

        // holder for column indexes to remove
        indexes = new HashSet<>();

    }

    @Override
    public Table transform(Table table) {
        if (!table.getName().matches(tablePattern)) {
            return (table);
        }
        int j = 0;
        for (Iterator<Column> i = table.getColumns().iterator(); i.hasNext(); ) {
            Column column = i.next();
            if (column.getName().matches(columnPattern)) {
                i.remove();
                indexes.add(j);
                report.column();
            }
            j++;
        }
        report.table();
        return (table);
    }

    @Override
    public Row transform(Table table, Row row) {
        if (!table.getName().matches(tablePattern)) {
            return (row);
        }
        int j = 0;
        for (Iterator<Object> i = row.getValues().iterator(); i.hasNext(); ) {
            i.next();
            if (indexes.contains(j)) {
                i.remove();
            }
            j++;
        }
        report.row();
        return (row);
    }

    @Override
    public void clean() {
        report.end();
    }

    @Override
    public Report report() {
        return (report);
    }

}
