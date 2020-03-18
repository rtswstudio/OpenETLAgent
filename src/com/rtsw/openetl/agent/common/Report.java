package com.rtsw.openetl.agent.common;

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

    private Map<String, String> extras = new HashMap<>();

    private Map<String, Integer> warnings = new HashMap<>();

    private Map<String, Integer> errors = new HashMap<>();

    public Report(String implementation) {
        this.implementation = implementation;
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
