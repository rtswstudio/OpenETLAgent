package com.rtsw.openetl.agent.summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtsw.openetl.agent.api.SummaryPusher;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Summary;

/**
 * @author RT Software Studio
 */
public class ConsoleSummaryPusher implements SummaryPusher {

    private ObjectMapper mapper = new ObjectMapper();

    private String title;

    private String description;

    private boolean pretty = true;

    @Override
    public void init(Configuration configuration) throws Exception {

        // required
        title = configuration.get("title", null);

        // optional
        description = configuration.get("description", null);

        // optional
        pretty = configuration.get("pretty", true);

    }

    @Override
    public void push(Summary summary) {
        summary.setTitle(title);
        summary.setDescription(description);
        try {
            if (pretty) {
                System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(summary));
            } else {
                System.out.println(mapper.writeValueAsString(summary));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}