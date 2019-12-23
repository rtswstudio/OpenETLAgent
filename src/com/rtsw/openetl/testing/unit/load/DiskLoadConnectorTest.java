package com.rtsw.openetl.testing.unit.load;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.format.CSVFormat;
import com.rtsw.openetl.agent.load.DiskLoadConnector;
import com.rtsw.openetl.testing.unit.common.Bootstrap;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author RT Software Studio
 */
public class DiskLoadConnectorTest {

    private static final String destination = "/tmp";

    @Test
    public void testLoad() throws Exception {

        // test destination
        File file = new File(destination);
        assertEquals(file.exists(), true);
        assertEquals(file.isDirectory(), true);
        assertEquals(file.canWrite(), true);

        Configuration configuration = new Configuration();
        configuration.set("destination", destination);
        configuration.set("compress", false);

        DiskLoadConnector load = new DiskLoadConnector();
        load.init(configuration);

        // for load, we need a format also

        configuration = new Configuration();
        configuration.set("column_separator", ";");
        configuration.set("line_separator", "\n");
        configuration.set("header", true);
        configuration.set("encoding", "UTF-8");

        CSVFormat format = new CSVFormat();
        format.init(configuration);

        load.load(Bootstrap.table(), format);

        load.load(Bootstrap.table(), Bootstrap.row(), format);

        load.clean();

        assertEquals(new File(destination, "person.csv").exists(), true);

    }

}
