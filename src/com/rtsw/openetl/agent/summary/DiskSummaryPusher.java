package com.rtsw.openetl.agent.summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtsw.openetl.agent.api.SummaryPusher;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Summary;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author RT Software Studio
 */
public class DiskSummaryPusher implements SummaryPusher {

    private ObjectMapper mapper = new ObjectMapper();

    private File destination;

    private String title;

    private String description;

    private String filename;

    private boolean compress = false;

    private String encoding;

    private boolean pretty = false;

    @Override
    public void init(Configuration configuration) throws Exception {

        // required
        destination = new File(configuration.get("destination", null));
        if (destination == null) {
            throw new Exception("missing required parameter 'destination'");
        }

        // required
        title = configuration.get("title", null);
        if (title == null) {
            throw new Exception("missing required parameter 'title'");
        }

        // optional
        description = configuration.get("description", null);

        // optional
        filename = configuration.get("filename", "summary.json");

        // optional
        compress = configuration.get("compress", false);

        // optional
        encoding = configuration.get("encoding", "UTF-8");

        // optional
        pretty = configuration.get("pretty", false);

    }

    @Override
    public void push(Summary summary) {
        summary.setTitle(title);
        summary.setDescription(description);
        OutputStream out = null;
        try {
            if (compress) {
                out = new GZIPOutputStream(new FileOutputStream(new File(destination, filename + ".gz"), false));
            } else {
                out = new BufferedOutputStream(new FileOutputStream(new File(destination, filename), false));
            }
            if (pretty) {
                out.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(summary).getBytes(encoding));
            } else {
                out.write(mapper.writeValueAsString(summary).getBytes(encoding));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

}