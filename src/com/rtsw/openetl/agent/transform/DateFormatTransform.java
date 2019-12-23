package com.rtsw.openetl.agent.transform;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.api.Transform;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author RT Software Studio
 */
public class DateFormatTransform implements Transform {

    private static final String ID = DateFormatTransform.class.getName();

    private Report report = new Report(ID);

    private SimpleDateFormat format;

    private String tablePattern;

    private String columnPattern;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        format = new SimpleDateFormat(configuration.get("format", null));

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

    }

    @Override
    public Table transform(Table table) {
        if (table.getName().matches(tablePattern)) {
            report.table();
        }
        for (Column column : table.getColumns()) {
            if (column.getName().matches(columnPattern)) {
                report.column();
            }
        }
        return (table);
    }

    @Override
    public Row transform(Table table, Row row) {
        if (!table.getName().matches(tablePattern)) {
            return (row);
        }
        for (int i = 0; i < row.getValues().size(); i++) {
            Column column = table.getColumns().get(i);
            if (!column.getName().matches(columnPattern)) {
                continue;
            }
            Object value = row.getValues().get(i);
            if (value instanceof Date) {
                row.getValues().set(i, format.format((Date) value));
            }
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
