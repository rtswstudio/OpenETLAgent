package com.rtsw.openetl.testing.unit.transform;

import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.transform.DropColumnTransform;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class DropColumnTransformTest {

    @Test
    public void testTransform() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("table_pattern", "Person");
        configuration.set("column_pattern", "Id");

        DropColumnTransform transform = new DropColumnTransform();
        transform.init(configuration);

        Table table = transform.transform(Bootstrap.table());

        assertEquals(table.getName(), "Person");
        assertEquals(table.getColumns().size(), 6);

        Row row = transform.transform(table, Bootstrap.row());

        assertEquals(row.getValues().size(), 6);

        transform.clean();

    }

}
