package com.rtsw.openetl.agent.api;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;

/**
 * @author RT Software Studio
 */
public interface ExtractConnector {

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
     * Start extracting data.
     *
     * @param agentListener The listener
     */
    void extract(AgentListener agentListener);

    /**
     * Clean any resources used.
     */
    void clean();

    /**
     * Return a report of the extract process.
     *
     * @return The report
     */
    Report report();

}
