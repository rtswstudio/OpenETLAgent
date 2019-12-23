package com.rtsw.openetl.testing.unit.transform;

import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.transform.DecimalFormatTransform;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class DecimalFormatTransformTest {

    @Test
    public void testTransform() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("format", "0.00");
        configuration.set("table_pattern", "Person");
        configuration.set("column_pattern", "Weight");

        DecimalFormatTransform transform = new DecimalFormatTransform();
        transform.init(configuration);

        Table table = transform.transform(Bootstrap.table());

        assertEquals(table.getName(), "Person");
        assertEquals(table.getColumns().size(), 7);

        Row row = transform.transform(table, Bootstrap.row());

        assertEquals(row.getValues().size(), 7);
        assertEquals(row.getValues().get(5).getClass().getName(), "java.lang.String");
        assertEquals(row.getValues().get(5).toString(), "87.50");

        transform.clean();

    }

}
