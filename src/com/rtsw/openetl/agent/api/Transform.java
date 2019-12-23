package com.rtsw.openetl.agent.api;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

/**
 * @author RT Software Studio
 */
public interface Transform {

    /**
     * Return an identifier for the transform.
     *
     * @return The identifier
     */
    String getId();

    /**
     * Initialize the transform.
     *
     * @param configuration The configuration
     * @throws Exception If the configuration is not valid
     */
    void init(Configuration configuration) throws Exception;

    /**
     * Transform a table.
     *
     * @param table The table
     * @return The transformed table
     */
    Table transform(Table table);

    /**
     * Transform a row.
     *
     * @param table The table
     * @param row The row
     * @return The transformed row
     */
    Row transform(Table table, Row row);

    /**
     * Clean up any resources used.
     */
    void clean();

    /**
     * Return a report of the transform process.
     *
     * @return The report
     */
    Report report();

}
