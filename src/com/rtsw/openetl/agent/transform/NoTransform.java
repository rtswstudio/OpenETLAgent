package com.rtsw.openetl.agent.transform;

import com.rtsw.openetl.agent.api.Transform;
import com.rtsw.openetl.agent.common.*;

/**
 * @author RT Software Studio
 */
public class NoTransform implements Transform {

    private static final String ID = NoTransform.class.getName();

    private Report report = new Report(ID);

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {
        report.start();
    }

    @Override
    public Table transform(Table table) {
        report.table();
        for (Column column : table.getColumns()) {
            report.column();
        }
        return (table);
    }

    @Override
    public Row transform(Table table, Row row) {
        report.row();
        return (row);
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
