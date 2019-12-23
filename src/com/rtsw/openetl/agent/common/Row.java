package com.rtsw.openetl.agent.common;

import java.util.List;

/**
 * @author RT Software Studio
 */
public class Row {

    private List<Object> values;

    public Row() {
    }

    public Row(List<Object> values) {
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

}
