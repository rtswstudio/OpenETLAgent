package com.rtsw.openetl.agent.transform;

import com.rtsw.openetl.agent.api.Transform;
import com.rtsw.openetl.agent.common.*;
import com.rtsw.openetl.agent.utils.ExceptionUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author RT Software Studio
 */
public class ReplaceColumnTransform implements Transform {

    private static final String ID = ReplaceColumnTransform.class.getName();

    private Report report = new Report(ID);

    private String tablePattern;

    private String columnPattern;

    private String replaceWhat;

    private String replaceWith;

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

        // required
        replaceWhat = configuration.get("replace_what", null);
        if (replaceWhat == null) {
            throw new Exception("missing required parameter 'replace_what'");
        }

        // required
        replaceWith = configuration.get("replace_with", null);
        if (replaceWith == null) {
            throw new Exception("missing required parameter 'replace_with'");
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
        int i = 0;
        for (Column column : table.getColumns()) {
            if (column.getName().matches(columnPattern)) {
                Object value = row.getValues().get(i);
                if (value instanceof String) {
                    String newValue = ((String) value).replaceAll(replaceWhat, replaceWith);
                    row.getValues().set(i, newValue);
                }
            }
            i++;
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
