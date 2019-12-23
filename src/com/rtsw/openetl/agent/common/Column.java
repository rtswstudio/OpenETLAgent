package com.rtsw.openetl.agent.common;

/**
 * @author RT Software Studio
 */
public class Column {

    private String name;

    private String typeName;

    private String className;

    public Column() {
    }

    public Column(String name, String typeName, String className) {
        this.name = name;
        this.typeName = typeName;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
