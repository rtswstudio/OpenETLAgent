package com.rtsw.openetl.testing.unit.transform;

import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.transform.RenameColumnTransform;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class RenameColumnTransformTest {

    @Test
    public void testTransform() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("table_pattern", "Person");
        configuration.set("column_pattern", "Id");
        configuration.set("new_name", "PersonId");

        RenameColumnTransform transform = new RenameColumnTransform();
        transform.init(configuration);

        Table table = transform.transform(Bootstrap.table());

        assertEquals(table.getName(), "Person");
        assertEquals(table.getColumns().size(), 7);
        assertEquals(table.getColumns().get(0).getName(), "PersonId");

        Row row = transform.transform(table, Bootstrap.row());

        assertEquals(row.getValues().size(), 7);
        assertEquals(row.getValues().get(0).getClass().getName(), "java.lang.Integer");
        assertEquals(row.getValues().get(0), 1);

        transform.clean();

    }

}
