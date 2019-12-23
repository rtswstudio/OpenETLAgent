package com.rtsw.openetl.agent.format;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.api.Format;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

import java.util.Date;

/**
 * @author RT Software Studio
 */
public class JSONFormat implements Format {

    private static final String ID = JSONFormat.class.getName();

    private Report report = new Report(ID);

    private boolean pretty = false;

    private String encoding;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public String getFileExtensionHint() {
        return ("json");
    }

    @Override
    public String getMimeTypeHint() {
        return ("application/json");
    }

    @Override
    public byte[] getHeader() {
        try {
            return ((pretty ? "[\n" : "[ ").getBytes(encoding));
        } catch (Exception e) {
            report.warning(e.getMessage());
            return ((pretty ? "[\n" : "[ ").getBytes());
        }
    }

    @Override
    public byte[] getHeaderSeparator() {
        return (null);
    }

    @Override
    public byte[] getRowSeparator() {
        try {
            return ((pretty ? ",\n" : ", ").getBytes(encoding));
        } catch (Exception e) {
            report.warning(e.getMessage());
            return ((pretty ? ",\n" : ", ").getBytes());
        }
    }

    @Override
    public byte[] getFooter() {
        try {
            return ((pretty ? "\n]" : " ]").getBytes(encoding));
        } catch (Exception e) {
            report.warning(e.getMessage());
            return ((pretty ? "\n]" : " ]").getBytes());
        }
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // optional
        pretty = configuration.get("pretty", false);

        // optional
        encoding = configuration.get("encoding", "UTF-8");

    }

    @Override
    public byte[] format(Table table) {
        report.table();
        return (null);
    }

    @Override
    public byte[] format(Table table, Row row) {
        StringBuilder sb = new StringBuilder();
        sb.append(pretty ? "{\n" : "{ ");
        int i = 0;
        for (Column c : table.getColumns()) {
            if (i > 0) {
                sb.append(pretty ? ",\n" : ", ");
            } else {
                report.column();
            }
            sb.append(pretty ? "\t" : "");
            sb.append("\"");
            sb.append(c.getName());
            sb.append("\": ");
            Object o = row.getValues().get(i);
            if (o instanceof String || o instanceof Date) {
                sb.append("\"");
                sb.append(o);
                sb.append("\"");
            } else {
                sb.append(o);
            }
            i++;
        }
        sb.append(pretty ? "\n}" : " }");
        try {
            byte[] b = sb.toString().getBytes(encoding);
            report.row();
            return (b);
        } catch (Exception e) {
            report.warning(e.getMessage());
            byte[] b = sb.toString().getBytes();
            report.row();
            return (b);
        }
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
