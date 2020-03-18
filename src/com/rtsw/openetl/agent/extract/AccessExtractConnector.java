package com.rtsw.openetl.agent.extract;

import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.api.ExtractConnector;
import com.rtsw.openetl.agent.common.*;

import java.io.File;

/**
 * @author RT Software Studio
 */
public class AccessExtractConnector implements ExtractConnector {

    private static final String ID = AccessExtractConnector.class.getName();

    private Report report = new Report(ID);

    private String source;

    private String filenamePattern;

    private String tablePattern;

    private boolean recursive = false;

    private AgentListener agentListener;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        source = configuration.get("source", null);
        if (source == null) {
            throw new Exception("missing required parameter 'source'");
        }

        // required
        filenamePattern = configuration.get("filename_pattern", null);
        if (filenamePattern == null) {
            throw new Exception("missing required parameter 'filename_pattern'");
        }

        // optional
        tablePattern = configuration.get("table_pattern", null);

        // optional
        recursive = configuration.get("recursive", false);

    }

    @Override
    public void extract(AgentListener agentListener) {

        // event listener
        this.agentListener = agentListener;

        File base = new File(source);

        // check that source exists
        if (!base.exists()) {
            report.error(String.format("Source '%s' does not exist", source));
            return;
        }

        // check that source is readable
        if (!base.canRead()) {
            report.error(String.format("Source '%s' is not readable", source));
            return;
        }

        // check that source is directory
        if (!base.isDirectory()) {
            report.error(String.format("Source '%s' is not a directory", source));
            return;
        }
        agentListener.onStart();

        if (recursive) {
            doTravel(base);
        } else {
            for (File file : base.listFiles()) {
                doFile(file);
            }
        }

        agentListener.onEnd();

    }

    private void doTravel(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                doTravel(f);
            }
        } else {
            doFile(file);
        }
    }

    private void doFile(File file) {
        if (!file.getName().matches(filenamePattern)) {
            return;
        }
        if (file.getName().toLowerCase().endsWith(".mdb")) {
            doAccess(file);
            return;
        }
        if (file.getName().toLowerCase().endsWith(".accdb")) {
            doAccess(file);
            return;
        }
        report.error(String.format("Unsupported Access file '%s", file.getAbsolutePath()));
    }

    private void doAccess(File file) {
        try {
            Database database = DatabaseBuilder.open(file);
            for (String tableName : database.getTableNames()) {
                if (tablePattern != null) {
                    if (!tableName.matches(tablePattern)) {
                        continue;
                    }
                }
                com.rtsw.openetl.agent.common.Table t = new com.rtsw.openetl.agent.common.Table();
                t.setName(tableName);
                Table table = database.getTable(tableName);
                for (Column column : table.getColumns()) {
                    t.getColumns().add(getColumn(column.getName(), column.getType()));
                }
                agentListener.onTable(t);
                report.table();
                for (Row row : table) {
                    com.rtsw.openetl.agent.common.Row r = new com.rtsw.openetl.agent.common.Row();
                    for (Column column : table.getColumns()) {
                        r.getValues().add(row.get(column.getName()));
                    }
                    agentListener.onRow(t, r);
                    report.row();
                }
            }
        } catch (Exception e) {
            report.error(e.getMessage());
        }
    }

    private com.rtsw.openetl.agent.common.Column getColumn(String name, DataType type) {
        com.rtsw.openetl.agent.common.Column c = new com.rtsw.openetl.agent.common.Column();
        c.setName(name);
        if (type == DataType.BIG_INT) {
            c.setTypeName("Long");
            c.setClassName("java.lang.Long");
        }
        if (type == DataType.LONG) {
            c.setTypeName("Long");
            c.setClassName("java.lang.Long");
        }
        if (type == DataType.INT) {
            c.setTypeName("Integer");
            c.setClassName("java.lang.Integer");
        }
        if (type == DataType.NUMERIC) {
            c.setTypeName("Integer");
            c.setClassName("java.lang.Integer");
        }
        if (type == DataType.BOOLEAN) {
            c.setTypeName("Boolean");
            c.setClassName("java.lang.Boolean");
        }
        if (type == DataType.TEXT) {
            c.setTypeName("String");
            c.setClassName("java.lang.String");
        }
        if (type == DataType.DOUBLE) {
            c.setTypeName("Double");
            c.setClassName("java.lang.Double");
        }
        if (type == DataType.FLOAT) {
            c.setTypeName("Float");
            c.setClassName("java.lang.Float");
        }
        if (type == DataType.SHORT_DATE_TIME) {
            c.setTypeName("Date");
            c.setClassName("java.util.Date");
        }
        return (c);
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
