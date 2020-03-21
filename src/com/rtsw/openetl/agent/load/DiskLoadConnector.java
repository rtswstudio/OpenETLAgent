package com.rtsw.openetl.agent.load;

import com.rtsw.openetl.agent.api.*;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author RT Software Studio
 */
public class DiskLoadConnector implements LoadConnector {

    private static final String ID = DiskLoadConnector.class.getName();

    private Report report = new Report(ID);

    private File destination;

    private boolean compress = false;

    private Map<String, OutputStream> streams;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        destination = new File(configuration.get("destination", null));
        if (destination == null) {
            throw new Exception("missing required parameter 'destination'");
        }
        if (!destination.exists()) {
            throw new Exception(String.format("destination '%s' does not exist", destination.getAbsolutePath()));
        }
        if (!destination.isDirectory()) {
            throw new Exception(String.format("destination '%s' is not a directory", destination.getAbsolutePath()));
        }
        if (!destination.canWrite()) {
            throw new Exception(String.format("destination '%s' does not have write permission", destination.getAbsolutePath()));
        }

        // optional
        compress = configuration.get("compress", false);

        streams = new HashMap<>();

    }

    @Override
    public void header(Format format) {
        if (format.getHeader() != null) {
            try {
                OutputStream out = streams.get(format.getHeader());
                out.write(format.getRowSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void rowSeparator(Table table, Format format) {
        if (table == null) {
            return;
        }
        if (format.getRowSeparator() != null) {
            try {
                OutputStream out = streams.get(table.getName());
                out.write(format.getRowSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void headerSeparator(Table table, Format format) {
        if (table == null) {
            return;
        }
        if (format.getHeaderSeparator() != null) {
            try {
                OutputStream out = streams.get(table.getName());
                out.write(format.getHeaderSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void footer(Format format) {
        if (format.getFooter() != null) {
            try {
                OutputStream out = streams.get(format.getFooter());
                out.write(format.getRowSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void load(Table table, Format format) {
        if (table == null) {
            return;
        }
        OutputStream out = null;
        try {
            if (compress) {
                out = new GZIPOutputStream(new FileOutputStream(new File(destination, table.getName() + "." + format.getFileExtensionHint() + ".gz"), false));
            } else {
                out = new BufferedOutputStream(new FileOutputStream(new File(destination, table.getName() + "." + format.getFileExtensionHint()), false));
            }
            out.write(format.format(table));
            streams.put(table.getName(), out);
            report.column(table.getColumns().size());
            report.table();
        } catch (Exception e) {
            report.error(e.getMessage());
        }
    }

    @Override
    public void load(Table table, Row row, Format format) {
        if (table == null || row == null) {
            return;
        }
        try {
            OutputStream out = streams.get(table.getName());
            out.write(format.format(table, row));
            report.row();
        } catch (Exception e) {
            report.error(e.getMessage());
        }
    }

    @Override
    public void clean() {
        for (OutputStream out : streams.values()) {
            try {
                out.close();
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
        report.end();
    }

    @Override
    public Report report() {
        return (report);
    }

}
