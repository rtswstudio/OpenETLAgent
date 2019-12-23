package com.rtsw.openetl.agent.common;

import com.rtsw.openetl.agent.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RT Software Studio
 */
public class Report {

    private String implementation;

    private String title;

    private String description;

    private Date start;

    private Date end;

    private long tables = 0;

    private long columns = 0;

    private long rows = 0;

    private Map<String, String> extras;

    private Map<String, Integer> warnings;

    private Map<String, Integer> errors;

    public static class Factory {

        private static String json(String name, Map map) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("\t\"%s\": {", name));
            int i = 0;
            for (Object key : map.keySet()) {
                if (i > 0) {
                    sb.append(",");
                }
                Object value = map.get(key);
                if (value instanceof String) {
                    sb.append(String.format("\n\t\t\"%s\": \"%s\"", key, value));
                } else {
                    sb.append(String.format("\n\t\t\"%s\": %d", key, value));
                }
                i++;
                if (i == map.keySet().size()) {
                    sb.append("\n\t");
                }
            }
            sb.append("}");
            return (sb.toString());
        }

        public static String json(Report report) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append(String.format("\t\"implementation\": \"%s\",\n", report.getImplementation()));
            sb.append(String.format("\t\"title\": \"%s\",\n", report.getTitle()));
            sb.append(String.format("\t\"description\": \"%s\",\n", report.getDescription()));
            sb.append(String.format("\t\"start\": \"%s\",\n", DateUtils.format(report.getStart())));
            sb.append(String.format("\t\"end\": \"%s\",\n", DateUtils.format(report.getEnd())));
            sb.append(String.format("\t\"tables\": %d,\n", report.getTables()));
            sb.append(String.format("\t\"columns\": %d,\n", report.getColumns()));
            sb.append(String.format("\t\"rows\": %d,\n", report.getRows()));

            sb.append(Factory.json("extras", report.getExtras()));
            sb.append(",\n");

            sb.append(Factory.json("warnings", report.getWarnings()));
            sb.append(",\n");

            sb.append(Factory.json("errors", report.getErrors()));

            sb.append("\n}");
            return (sb.toString());
        }

    }

    public Report(String implementation) {
        this.implementation = implementation;
        extras = new HashMap<>();
        warnings = new HashMap<>();
        errors = new HashMap<>();
    }

    public void warning(String message) {
        Integer i = warnings.get(message);
        if (i == null) {
            warnings.put(message, 1);
        } else {
            warnings.put(message, i + 1);
        }
    }

    public void error(String message) {
        Integer i = errors.get(message);
        if (i == null) {
            errors.put(message, 1);
        } else {
            errors.put(message, i + 1);
        }
    }

    /**
     *
     */
    public void start() {
       start = new Date();
    }

    /**
     *
     */
    public void end() {
        end = new Date();
    }

    /**
     *
     */
    public void table() {
       tables++;
    }

    public void table(int i) {
        tables += i;
    }

    /**
     *
     */
    public void column() {
        columns++;
    }

    /**
     *
     * @param i
     */
    public void column(int i) {
        columns += i;
    }

    /**
     *
     */
    public void row() {
        rows++;
    }

    /**
     *
     * @param i
     */
    public void row(int i) {
        rows += i;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public long getTables() {
        return tables;
    }

    public void setTables(long tables) {
        this.tables = tables;
    }

    public long getColumns() {
        return columns;
    }

    public void setColumns(long columns) {
        this.columns = columns;
    }

    public long getRows() {
        return rows;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public Map<String, Integer> getWarnings() {
        return warnings;
    }

    public void setWarnings(Map<String, Integer> warnings) {
        this.warnings = warnings;
    }

    public Map<String, Integer> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Integer> errors) {
        this.errors = errors;
    }

}
