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
public class CSVFormat implements Format {

    private static final String ID = CSVFormat.class.getName();

    private Report report = new Report(ID);

    private String columnSeparator;

    private String lineSeparator;

    private boolean header = true;

    private String encoding;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public String getFileExtensionHint() {
        return ("csv");
    }

    @Override
    public String getMimeTypeHint() {
        return ("text/csv");
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
        try {
            return (lineSeparator.getBytes(encoding));
        } catch (Exception e) {
            report.warning(e.getMessage());
            return (lineSeparator.getBytes());
        }
    }

    @Override
    public byte[] getFooter() {
        return (null);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // optional
        columnSeparator = configuration.get("column_separator", ";");

        // optional
        lineSeparator = configuration.get("line_separator", "\n");

        // optional
        header = configuration.get("header", true);

        // optional
        encoding = configuration.get("encoding", "UTF-8");

    }

    @Override
    public byte[] format(Table table) {
        if (!header) {
            return (new byte[0]);
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Column c : table.getColumns()) {
            if (i > 0) {
                sb.append(columnSeparator);
            }
            sb.append(c.getName());
            report.column();
            i++;
        }
        try {
            byte[] b = sb.toString().getBytes(encoding);
            report.table();
            return (b);
        } catch (Exception e) {
            report.warning(e.getMessage());
            byte[] b = sb.toString().getBytes();
            report.table();
            return (b);
        }
    }

    @Override
    public byte[] format(Table table, Row row) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object o : row.getValues()) {
            if (i > 0) {
                sb.append(columnSeparator);
            }
            sb.append(o);
            i++;
        }
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
