package com.rtsw.openetl.testing.unit.transform;

import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.transform.DateFormatTransform;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class DateFormatTransformTest {

    @Test
    public void testTransform() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("format", "dd.MM.yyyy");
        configuration.set("table_pattern", "Person");
        configuration.set("column_pattern", "DateOfBirth");

        DateFormatTransform transform = new DateFormatTransform();
        transform.init(configuration);

        Table table = transform.transform(Bootstrap.table());

        assertEquals(table.getName(), "Person");
        assertEquals(table.getColumns().size(), 7);

        Row row = transform.transform(table, Bootstrap.row());

        assertEquals(row.getValues().size(), 7);
        assertEquals(row.getValues().get(3).getClass().getName(), "java.lang.String");
        assertEquals(row.getValues().get(3).toString(), "01.01.1980");

        transform.clean();

    }

}
