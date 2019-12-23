package com.rtsw.openetl.agent.api;

import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Summary;

/**
 * @author RT Software Studio
 */
public interface SummaryPusher {

    /**
     * Initialize the summary pusher.
     *
     * @param configuration The configuration
     * @throws Exception If the configuration is not valid
     */
    void init(Configuration configuration) throws Exception;

    /**
     * Push the summary.
     *
     * @param summary The summary
     */
    void push(Summary summary);

}
