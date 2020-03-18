package com.rtsw.openetl.agent.common;

import com.rtsw.openetl.agent.common.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RT Software Studio
 */
public class Table {

    private String name;

    private List<Column> columns = new ArrayList<>();

    public Table() {
    }

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /* helper methods */

    public Column getColumn(int index) {
        return (columns.get(index));
    }

    public String getColumnName(int index) {
        return (getColumn(index).getName());
    }

    public String getTypeName(int index) {
        return (getColumn(index).getTypeName());
    }

    public String getClassName(int index) {
        return (getColumn(index).getClassName());
    }

}
