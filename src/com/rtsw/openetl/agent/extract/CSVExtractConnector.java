package com.rtsw.openetl.agent.extract;

import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.api.ExtractConnector;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.utils.ExceptionUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author RT Software Studio
 */
public class CSVExtractConnector implements ExtractConnector {

    private static final String ID = CSVExtractConnector.class.getName();

    private Report report = new Report(ID);

    private String source;

    private String filenamePattern;

    private boolean header = true;

    private boolean recursive = false;

    private String separator;

    private boolean dropExtension = true;

    private AgentListener agentListener;

    private boolean inferSchema = true;

    private int inferSchemaRows = 10000;

    private String inferSchemaDatePattern;

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
        header = configuration.get("header", true);

        // optional
        recursive = configuration.get("recursive", false);

        // optional
        separator = configuration.get("separator", ";");

        // optional
        dropExtension = configuration.get("drop_extension", true);

        // optional
        inferSchema = configuration.get("infer_schema", true);

        // optional
        inferSchemaRows = configuration.get("infer_schema_rows", 10000);

        // optional
        inferSchemaDatePattern = configuration.get("infer_Schema_date_pattern", "dd.MM.yyyy");

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

    @Override
    public void clean() {
        report.end();
    }

    @Override
    public Report report() {
        return (report);
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
        String tableName = file.getName();
        if (dropExtension && tableName.contains(".")) {
            tableName = tableName.substring(0, tableName.indexOf("."));
        }
        BufferedReader in = null;
        Map<Integer, Type> types = new HashMap<>();
        if (inferSchema) {
            try {
                in = new BufferedReader(new FileReader(file));
                String line = null;
                int i = 0;
                while ((line = in.readLine()) != null && i < inferSchemaRows) {
                    if (i == 0 && header) {
                        // Ignore header line
                    } else {
                        StringTokenizer st = new StringTokenizer(line, separator);
                        int j = 0;
                        while (st.hasMoreTokens()) {
                            String s = st.nextToken();
                            Type type = getType(s);
                            if (types.containsKey(j) && types.get(j) != null && !types.get(j).equals(type)) {
                                types.put(j, null);
                            } else {
                                types.put(j, type);
                            }
                            j++;
                        }
                    }
                    i++;
                }
            } catch (Exception e) {
                report.warning(e.getMessage());
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    report.warning(e.getMessage());
                }
            }
        }
        try {
            in = new BufferedReader(new FileReader(file));
            String line = null;
            int i = 0;
            Table table = null;
            while ((line = in.readLine()) != null) {
                List<Object> items = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(line, separator);
                while (st.hasMoreTokens()) {
                    items.add(st.nextToken());
                }
                if (i == 0) {
                    List<Column> columns = new ArrayList<>();
                    if (header) {
                        for (int j = 0; j < items.size(); j++) {
                            Type type = types.get(j);
                            if (type == null) {
                                columns.add(new Column(items.get(j).toString(), "String", "java.lang.String"));
                            } else {
                                columns.add(new Column(items.get(j).toString(), types.get(j).getTypeName(), types.get(j).getClassName()));
                            }
                        }
                        table = new Table(tableName, columns);
                        agentListener.onTable(table);
                    } else {
                        for (int j = 0; j < items.size(); j++) {
                            Type type = types.get(j);
                            if (type == null) {
                                columns.add(new Column("" + j, "String", "java.lang.String"));
                            } else {
                                columns.add(new Column("" + j, types.get(j).getTypeName(), types.get(j).getClassName()));
                            }
                        }
                        table = new Table(tableName, columns);
                        agentListener.onTable(table);
                        Row row = null;
                        if (inferSchema) {
                            row = new Row(convertItems(types, items));
                        } else {
                            row = new Row(items);
                        }
                        agentListener.onRow(table, row);
                    }
                    report.column(columns.size());
                } else {
                    Row row = null;
                    if (inferSchema) {
                        row = new Row(convertItems(types, items));
                    } else {
                        row = new Row(items);
                    }
                    agentListener.onRow(table, row);
                }
                report.row();
                i++;
            }
            report.table();
        } catch (Exception e) {
            report.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                report.warning(e.getMessage());
            }
        }
    }

    private List<Object> convertItems(Map<Integer, Type> types, List<Object> items) {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Type type = types.get(i);
            if (type == null) {
                result.add(items.get(i));
            } else {
                String s = items.get(i).toString();
                switch (type.getTypeName()) {
                    case "String": result.add(s); break;
                    case "Integer": {
                        try {
                            result.add(new Integer(s));
                        } catch (Exception e) {
                            result.add(s);
                            ExceptionUtils.handleException(e);
                        }
                        break;
                    }
                    case "Double": {
                        try {
                            result.add(new Double(s));
                        } catch (Exception e) {
                            result.add(s);
                            ExceptionUtils.handleException(e);
                        }
                        break;
                    }
                    case "Boolean": {
                        try {
                            result.add(new Boolean(s));
                        } catch (Exception e) {
                            result.add(s);
                            ExceptionUtils.handleException(e);
                        }
                        break;
                    }
                    case "Date": {
                        try {
                            result.add(new SimpleDateFormat(inferSchemaDatePattern).parse(s));
                        } catch (Exception e) {
                            result.add(s);
                            ExceptionUtils.handleException(e);
                        }
                        break;
                    }
                }
            }
        }
        return (result);
    }

    /**
     * https://stackoverflow.com/questions/2811031/decimal-or-numeric-values-in-regular-expression-validation/39399503#39399503
     *
     * @param s
     * @return
     */
    private Type getType(String s) {
        if (s == null) {
            return (new Type("String", "java.lang.String"));
        }
        if (s.matches("^-?(0|[1-9]\\d*)(e-?(0|[1-9]\\d*))?$")) {
            return (new Type("Integer", "java.lang.Integer"));
        }
        if (s.matches("^-?(0|[1-9]\\d*)(\\.\\d+)?(e-?(0|[1-9]\\d*))?$")) {
            return (new Type("Double", "java.lang.Double"));
        }
        if (s.toLowerCase().matches("true|false")) {
            return (new Type("Boolean", "java.lang.Boolean"));
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(inferSchemaDatePattern);
            sdf.parse(s);
            return (new Type("Date", "java.util.Date"));
        } catch (Exception e) {
            // TODO
        }
        return (new Type("String", "java.lang.String"));
    }

    private class Type {

        private String typeName;

        private String className;

        public Type(String typeName, String className) {
            this.typeName = typeName;
            this.className = className;
        }

        @Override
        public String toString() {
            return (typeName + " (" + className + ")");
        }

        @Override
        public boolean equals(Object obj) {
            Type type = (Type) obj;
            return (this.typeName.equals(type.typeName));
        }


        public String getTypeName() {
            return typeName;
        }

        public String getClassName() {
            return className;
        }

    }

}
