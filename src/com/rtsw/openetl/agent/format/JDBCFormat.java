package com.rtsw.openetl.agent.format;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.api.Format;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

/**
 * @author RT Software Studio
 */
public class JDBCFormat implements Format {

    private static final String ID = JDBCFormat.class.getName();

    private Report report = new Report(ID);

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public String getFileExtensionHint() {
        return (".ql");
    }

    @Override
    public String getMimeTypeHint() {
        return ("text/plain");
    }

    @Override
    public byte[] getHeader() {
        return (null);
    }

    @Override
    public byte[] getHeaderSeparator() {
        return (null);
    }

    @Override
    public byte[] getRowSeparator() {
        return (null);
    }

    @Override
    public byte[] getFooter() {
        return (null);
    }

    @Override
    public void init(Configuration configuration) throws Exception {
        report.start();
    }

    @Override
    public byte[] format(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(table.getName());
        sb.append(" (");
        int i = 0;
        for (Column column : table.getColumns()) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(column.getName());
            sb.append(" ");
            sb.append(column.getTypeName().equals("VARCHAR") ? "VARCHAR(255)" : column.getTypeName());
            report.column();
            i++;
        }
        sb.append(")");
        report.table();
        return (sb.toString().getBytes());
    }

    @Override
    public byte[] format(Table table, Row row) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(table.getName());
        sb.append(" values (");
        for (int i = 0; i < row.getValues().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
        report.row();
        return (sb.toString().getBytes());
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
