package com.rtsw.openetl.agent.api;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

/**
 * @author RT Software Studio
 */
public interface Format {

    /**
     * Return an identifier for the format.
     *
     * @return The identifier
     */
    String getId();

    /**
     * Return a file extension hint for the format (e.g. "json").
     *
     * @return The file extension hint
     */
    String getFileExtensionHint();

    /**
     * Return a mime type hint for the format (e.g. "application/json").
     * @return
     */
    String getMimeTypeHint();

    /**
     * Initialize the format.
     *
     * @param configuration The configuration
     * @throws Exception If the configuration is not valid
     */
    void init(Configuration configuration) throws Exception;

    /**
     * Format the header.
     *
     * @return The formatted header
     */
    byte[] getHeader();

    /**
     * Format the separator between the header and rows.
     *
     * @return The formatted separator
     */
    byte[] getHeaderSeparator();

    /**
     * Format the separator between each row.
     *
     * @return The formatted separator
     */
    byte[] getRowSeparator();

    /**
     * Format the footer.
     *
     * @return The formatted footer
     */
    byte[] getFooter();

    /**
     * Format a table.
     *
     * @param table The table
     * @return The formatted table
     */
    byte[] format(Table table);

    /**
     * Format a row.
     *
     * @param table The table
     * @param row The row
     * @return The formatted row
     */
    byte[] format(Table table, Row row);

    /**
     * Clean up any resources used.
     */
    void clean();

    /**
     * Return a report of the format process.
     *
     * @return The report
     */
    Report report();

}
