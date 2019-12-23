package com.rtsw.openetl.agent.api;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

/**
 * @author RT Software Studio
 */
public interface LoadConnector {

    /**
     * Return an identifier for the connector.
     *
     * @return The identifier
     */
    String getId();

    /**
     * Initialize the connector.
     *
     * @param configuration The configuration
     * @throws Exception If the configuration is not valid
     */
    void init(Configuration configuration) throws Exception;

    /**
     * Load the header.
     *
     * @param format The format
     */
    void header(Format format);

    /**
     * Separate the header from the rows.
     *
     * @param table The table
     * @param format The format
     */
    void headerSeparator(Table table, Format format);

    /**
     * Separate rows from each other.
     *
     * @param table The table
     * @param format The format
     */
    void rowSeparator(Table table, Format format);

    /**
     * Load the footer.
     *
     * @param format The format
     */
    void footer(Format format);

    /**
     * Load the table.
     *
     * @param table The table
     * @param format The format
     */
    void load(Table table, Format format);

    /**
     * Load a row.
     *
     * @param table The table
     * @param row The row
     * @param format The format
     */
    void load(Table table, Row row, Format format);

    /**
     * Clean up any resources used (after each table).
     *
     * @param table The table
     */
    void clean(Table table);

    /**
     * Clean up any resources used.
     */
    void clean();

    /**
     * Return a report of the load process.
     *
     * @return The report
     */
    Report report();

}
