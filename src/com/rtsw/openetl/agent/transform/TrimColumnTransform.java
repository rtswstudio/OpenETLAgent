package com.rtsw.openetl.agent.transform;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.api.Transform;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

/**
 * @author RT Software Studio
 */
public class TrimColumnTransform implements Transform {

    private static final String ID = TrimColumnTransform.class.getName();

    private Report report = new Report(ID);

    private String tablePattern;

    private String columnPattern;

    private boolean trimName = true;

    private boolean trimValue = true;

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

        // optional
        trimName = configuration.get("trim_name", true);

        // optional
        trimValue = configuration.get("trim_value", true);

    }

    @Override
    public Table transform(Table table) {
        if (!table.getName().matches(tablePattern)) {
            return (table);
        }
        int i = 0;
        for (Column column : table.getColumns()) {
            if (column.getName().matches(columnPattern)) {
                table.getColumns().set(i, new Column(trim(column.getName()), column.getTypeName(), column.getClassName()));
                report.column();
            }
            i++;
        }
        report.table();
        return (table);
    }

    @Override
    public Row transform(Table table, Row row) {
        if (!table.getName().matches(tablePattern)) {
            return (row);
        }
        int i = 0;
        for (Column column : table.getColumns()) {
            if (column.getName().matches(columnPattern) && column.getTypeName().equals("String")) {
                row.getValues().set(i, trim((String) row.getValues().get(i)));
            }
            i++;
        }
        return (row);
    }

    private String trim(String s) {
        if (s == null || s.isEmpty()) {
            return ("");
        }
        s = s.trim();
        if (s.startsWith("\"")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return (s);
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
