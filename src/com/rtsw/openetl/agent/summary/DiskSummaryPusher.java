package com.rtsw.openetl.agent.summary;

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

    private File destination;

    private String filename;

    private boolean compress = false;

    private String encoding;

    @Override
    public void init(Configuration configuration) throws Exception {

        // required
        destination = new File(configuration.get("destination", null));

        // optional
        filename = configuration.get("filename", "summary.json");

        // optional
        compress = configuration.get("compress", false);

        // optional
        encoding = configuration.get("encoding", "UTF-8");

    }

    @Override
    public void push(Summary summary) {
        OutputStream out = null;
        try {
            if (compress) {
                out = new GZIPOutputStream(new FileOutputStream(new File(destination, filename + ".gz"), false));
            } else {
                out = new BufferedOutputStream(new FileOutputStream(new File(destination, filename), false));
            }
            out.write(Summary.Factory.json(summary).getBytes(encoding));
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