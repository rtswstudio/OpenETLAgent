package com.rtsw.openetl.agent.format;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.api.Format;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author RT Software Studio
 */
public class JDBCFormat implements Format {

    private static final String ID = JDBCFormat.class.getName();

    private Report report = new Report(ID);

    private String dateFormat;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public String getFileExtensionHint() {
        return ("ql");
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
        return ("\n".getBytes());
    }

    @Override
    public byte[] getRowSeparator() {
        return ("\n".getBytes());
    }

    @Override
    public byte[] getFooter() {
        return (null);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // optional
        dateFormat = configuration.get("date_format", "yyyy-MM-dd HH:mm:ss");

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
            sb.append(getSQLType(column.getClassName()));
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
            Object o = row.getValues().get(i);
            if (o instanceof String) {
                sb.append("'");
                sb.append(o);
                sb.append("'");
            } else if (o instanceof Date) {
                sb.append("'");
                sb.append(new SimpleDateFormat(dateFormat).format((Date) o));
                sb.append("'");
            } else {
                sb.append(o);
            }
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

    private String getSQLType(String type) {
        switch (type) {
            case "java.lang.String": return "varchar";
            case "java.math.BigDecimal": return "decimal";
            case "java.lang.Boolean": return "boolean";
            case "java.lang.Integer": return "integer";
            case "java.lang.Long": return "bigint";
            case "java.lang.Float": return "real";
            case "java.lang.Double": return "double";
            case "java.util.Date": return "timestamp";
            default: return "varchar";
        }
    }

}
