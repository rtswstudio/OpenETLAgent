package com.rtsw.openetl.agent.summary;

import com.rtsw.openetl.agent.api.SummaryPusher;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Summary;

/**
 * @author RT Software Studio
 */
public class ConsoleSummaryPusher implements SummaryPusher {

    @Override
    public void init(Configuration configuration) throws Exception {

    }

    @Override
    public void push(Summary summary) {
        System.out.println(Summary.Factory.json(summary));
    }

}