package com.rtsw.openetl.testing.unit.transform;

import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.transform.TrimColumnTransform;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class TrimColumnTransformTest {

    @Test
    public void testTransform() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("table_pattern", "Person");
        configuration.set("column_pattern", "Firstname");
        configuration.set("trim_name", true);
        configuration.set("trim_value", true);

        TrimColumnTransform transform = new TrimColumnTransform();
        transform.init(configuration);

        Table table = Bootstrap.table();
        table.getColumn(1).setName(" Firstname ");
        table = transform.transform(Bootstrap.table());

        assertEquals(table.getName(), "Person");
        assertEquals(table.getColumns().size(), 7);
        assertEquals(table.getColumns().get(1).getName(), "Firstname");

        Row row = Bootstrap.row();
        row.getValues().set(1, "\"John\"");
        row = transform.transform(table, row);

        assertEquals(row.getValues().size(), 7);
        assertEquals(row.getValues().get(1).getClass().getName(), "java.lang.String");
        assertEquals(row.getValues().get(1).toString(), "John");

        transform.clean();

    }

}
