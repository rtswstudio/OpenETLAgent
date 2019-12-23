package com.rtsw.openetl.testing.unit;

import com.rtsw.openetl.testing.unit.format.CSVFormatTest;
import com.rtsw.openetl.testing.unit.format.JSONFormatTest;
import com.rtsw.openetl.testing.unit.load.DiskLoadConnectorTest;
import com.rtsw.openetl.testing.unit.transform.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        DateFormatTransformTest.class,
        DecimalFormatTransformTest.class,
        DropColumnTransformTest.class,
        MaskColumnTransformTest.class,
        RenameColumnTransformTest.class,
        TrimColumnTransformTest.class,
        CSVFormatTest.class,
        JSONFormatTest.class,
        DiskLoadConnectorTest.class
})

/**
 * @author RT Software Studio
 */
public class TestSuite {
}
