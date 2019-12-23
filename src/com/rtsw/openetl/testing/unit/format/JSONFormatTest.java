package com.rtsw.openetl.testing.unit.format;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.format.CSVFormat;
import com.rtsw.openetl.agent.format.JSONFormat;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class JSONFormatTest {

    @Test
    public void testFormat() throws Exception {

        Configuration configuration = new Configuration();
        configuration.set("pretty", false);
        configuration.set("encoding", "UTF-8");

        JSONFormat format = new JSONFormat();
        format.init(configuration);

        byte[] head = format.format(Bootstrap.table());

        assertEquals(head, null);

        byte[] data = format.format(Bootstrap.table(), Bootstrap.row());
        assertEquals(new String(data), "{ \"Id\": 1, \"Firstname\": \"John\", \"Lastname\": \"Doe\", \"DateOfBirth\": \"Tue Jan 01 00:00:00 EET 1980\", \"Alive\": true, \"Weight\": 87.5, \"Height\": 180.0 }");

        format.clean();

    }

}
