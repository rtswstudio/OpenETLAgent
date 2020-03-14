package com.rtsw.openetl.agent.extract;

import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.api.ExtractConnector;
import com.rtsw.openetl.agent.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author RT Software Studio
 */
public class RandomExtractConnector implements ExtractConnector {

    private static final String ID = RandomExtractConnector.class.getName();

    private Report report = new Report(ID);

    private int minRows = 50;

    private int maxRows = 100;

    private int minValue = 50;

    private int maxValue = 100;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // optional
        minRows = configuration.get("min_rows", 50);

        // optional
        maxRows = configuration.get("max_rows", 100);

        // optional
        minValue = configuration.get("min_value", 50);

        // optional
        maxValue = configuration.get("max_value", 100);

    }

    @Override
    public void extract(AgentListener agentListener) {
        Random random = new Random(System.currentTimeMillis());
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Index", "Integer", "java.lang.Integer"));
        columns.add(new Column("Number", "Integer", "java.lang.Integer"));
        Table table = new Table("RandomNumbers", columns);
        agentListener.onTable(table);
        report.table();
        for (int i = 0; i < random.nextInt(maxRows - minRows) + minRows; i++)  {
            List<Object> values = new ArrayList<>();
            values.add(i);
            values.add(random.nextInt(maxValue - minValue) + minValue);
            Row row = new Row(values);
            agentListener.onRow(table, row);
            report.row();
        }
    }

    @Override
    public void clean() {
        report.end();
    }

    @Override
    public Report report() {
        return(report);
    }

}
