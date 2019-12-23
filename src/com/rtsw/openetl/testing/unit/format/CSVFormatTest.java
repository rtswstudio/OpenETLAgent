package com.rtsw.openetl.testing.unit.format;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.format.CSVFormat;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class CSVFormatTest {

    @Test
    public void testFormat() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("column_separator", ";");
        configuration.set("line_separator", "\n");
        configuration.set("header", true);
        configuration.set("encoding", "UTF-8");

        CSVFormat format = new CSVFormat();
        format.init(configuration);

        byte[] head = format.format(Bootstrap.table());

        assertEquals(new String(head), "Id;Firstname;Lastname;DateOfBirth;Alive;Weight;Height");

        byte[] data = format.format(Bootstrap.table(), Bootstrap.row());
        assertEquals(new String(data), "1;John;Doe;Tue Jan 01 00:00:00 EET 1980;true;87.5;180.0");

        format.clean();

    }

}
