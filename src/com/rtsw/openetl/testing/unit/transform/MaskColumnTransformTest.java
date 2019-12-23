package com.rtsw.openetl.testing.unit.transform;

import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.transform.MaskColumnTransform;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class MaskColumnTransformTest {

    @Test
    public void testTransform() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("table_pattern", "Person");
        configuration.set("column_pattern", "Lastname");
        configuration.set("mask_policy", "hide");

        MaskColumnTransform transform = new MaskColumnTransform();
        transform.init(configuration);

        Table table = transform.transform(Bootstrap.table());

        assertEquals(table.getName(), "Person");
        assertEquals(table.getColumns().size(), 7);

        Row row = transform.transform(table, Bootstrap.row());

        assertEquals(row.getValues().size(), 7);
        assertEquals(row.getValues().get(2).getClass().getName(), "java.lang.String");
        assertEquals(row.getValues().get(2).toString(), "***");

        transform.clean();

    }

}
