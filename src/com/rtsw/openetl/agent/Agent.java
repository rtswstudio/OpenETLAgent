package com.rtsw.openetl.agent;

import com.rtsw.openetl.agent.api.*;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Summary;
import com.rtsw.openetl.agent.common.Table;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RT Software Studio
 */
public class Agent implements AgentListener {

    private long tables = 0;

    private long rows = 0;

    private Configuration loadConfiguration;

    private ExtractConnector extractConnector;

    private List<Transform> transforms = new ArrayList<>();

    private Format format;

    private LoadConnector loadConnector;

    private List<SummaryPusher> summaryPushers = new ArrayList<>();

    public static class Builder {

        private String extract;

        private List<String> transforms = new ArrayList<>();

        private String format;

        private String load;

        private List<String> summaries = new ArrayList<>();

        public Builder() {
        }

        public Builder extract(String path) {
            extract = path;
            return (this);
        }

        public Builder transform(String path) {
            transforms.add(path);
            return (this);
        }

        public Builder transforms(List<String> paths) {
            transforms = paths;
            return (this);
        }

        public Builder format(String path) {
            format = path;
            return (this);
        }

        public Builder load(String path) {
            load = path;
            return (this);
        }

        public Builder summaries(List<String> paths) {
            summaries = paths;
            return (this);
        }

        public Agent build() throws Exception {
            return (new Agent(extract, transforms, format, load, summaries));
        }

    }

    private Agent(String extract, List<String> transforms, String format, String load, List<String> summaries) throws Exception {

        // initialize extract
        Configuration extractConfiguration = new Configuration(new File(extract));
        this.extractConnector = getExtractConnector(extractConfiguration.get("implementation", null));
        this.extractConnector.init(extractConfiguration);

        // initialize transforms
        for (String transform : transforms) {
            Configuration transformConfiguration = new Configuration(new File(transform));
            this.transforms.add(getTransform(transformConfiguration.get("implementation", null)));
            this.transforms.get(this.transforms.size() - 1).init(transformConfiguration);
        }

        // initialize format
        Configuration formatConfiguration = new Configuration(new File(format));
        this.format = getFormat(formatConfiguration.get("implementation", null));
        this.format.init(formatConfiguration);

        // initialize load
        loadConfiguration = new Configuration(new File(load));
        this.loadConnector = getLoadConnector(loadConfiguration.get("implementation", null));
        this.loadConnector.init(loadConfiguration);

        // initialize summaries
        for (String summary : summaries) {
            Configuration summaryConfiguration = new Configuration(new File(summary));
            this.summaryPushers.add(getSummaryPusher(summaryConfiguration.get("implementation", null)));
            this.summaryPushers.get(this.summaryPushers.size() - 1).init(summaryConfiguration);
        }
    }

    public void run() {

        Summary summary = new Summary();

        extractConnector.extract(this);

        // clean
        extractConnector.clean();
        format.clean();
        for (Transform transform : transforms) {
            transform.clean();
        }
        loadConnector.clean();

        // reports
        summary.getReports().add(extractConnector.report());
        for (Transform transform : transforms) {
            summary.getReports().add(transform.report());
        }
        summary.getReports().add(format.report());
        summary.getReports().add(loadConnector.report());

        // summary
        for (SummaryPusher summaryPusher : summaryPushers) {
            summaryPusher.push(summary);
        }

    }

    private ExtractConnector getExtractConnector(String implementation) throws Exception {
        Class c = Class.forName(implementation);
        ExtractConnector extractConnector = (ExtractConnector) c.newInstance();
        return (extractConnector);
    }

    private Transform getTransform(String implementation) throws Exception {
        Class c = Class.forName(implementation);
        Transform transform = (Transform) c.newInstance();
        return (transform);
    }

    private Format getFormat(String implementation) throws Exception {
        Class c = Class.forName(implementation);
        Format format = (Format) c.newInstance();
        return (format);
    }

    private LoadConnector getLoadConnector(String implementation) throws Exception {
        Class c = Class.forName(implementation);
        LoadConnector loadConnector = (LoadConnector) c.newInstance();
        return (loadConnector);
    }

    private SummaryPusher getSummaryPusher(String implementation) throws Exception {
        Class c = Class.forName(implementation);
        SummaryPusher summaryPusher = (SummaryPusher) c.newInstance();
        return (summaryPusher);
    }

    @Override
    public void onStart() {
        loadConnector.header(format);
    }

    @Override
    public void onEnd() {
        loadConnector.footer(format);
    }

    @Override
    public void onTable(Table table) {

        tables++;
        rows = 0;

        // transform
        for (Transform transform : transforms) {
            table = transform.transform(table);
        }

        // load
        loadConnector.load(table, format);

        // separate header from first row
        loadConnector.headerSeparator(table, format);

    }

    @Override
    public void onRow(Table table, Row row) {

        // separate rows
        if (rows > 0) {
            loadConnector.rowSeparator(table, format);
        }

        rows++;

        // transform
        for (Transform transform : transforms) {
            row = transform.transform(table, row);
        }

        // load
        loadConnector.load(table, row, format);

    }

    public static void main(String[] args) throws Exception {

        if (args.length == 0 || args.length % 2 != 0) {
            System.out.println("Error: Invalid number of arguments\n");
            usage();
            return;
        }

        List<String> extracts = new ArrayList<>();
        List<String> transforms = new ArrayList<>();
        List<String> formats = new ArrayList<>();
        List<String> loads = new ArrayList<>();
        List<String> summaries = new ArrayList<>();

        String key = null;
        String value = null;
        int i = 0;
        for (String arg : args) {
            if (i % 2 == 0) {
                key = arg;
            } else {
                value = arg;
                switch (key) {
                    case "-e": extracts.add(value); break;
                    case "-t": transforms.add(value); break;
                    case "-f": formats.add(value); break;
                    case "-l": loads.add(value); break;
                    case "-s": summaries.add(value); break;
                    default: {
                        System.out.println("Error: Invalid argument\n");
                        usage();
                    } return;
                }
            }
            i++;
        }

        if (extracts.size() != 1) {
            System.err.println("Error: 1 extract connector configuration is required\n");
            usage();
            return;
        }

        if (formats.size() != 1) {
            System.err.println("Error: 1 format configuration is required\n");
            usage();
            return;
        }

        if (loads.size() != 1) {
            System.err.println("Error: 1 load connector configuration is required\n");
            usage();
            return;
        }

        Agent agent = new Builder()
                .extract(extracts.get(0))
                .transforms(transforms)
                .format(formats.get(0))
                .load(loads.get(0))
                .summaries(summaries)
                .build();

        agent.run();

    }

    private static void usage() {
        PrintStream out = System.out;
        out.println("Usage:");
        out.println();
        out.println("  java -cp openetl_[VERSION].jar com.rtsw.openetl.agent.Agent -e [EXTRACT] -t [TRANSFORM] -f [FORMAT] -l [LOAD] -s [SUMMARY]");
        out.println();
        out.println("Where:");
        out.println();
        out.println("  VERSION   is the version of OpenETL you are using, for example 0.1.0");
        out.println("  EXTRACT   is the path to your extract connector configuration, for example /tmp/extract.properties, (1 required)");
        out.println("  TRANSFORM is the path to your transform configuration, for example /tmp/transform.properties, (at least 1 required)");
        out.println("  FORMAT    is the path to your format configuration, for example /tmp/format.properties, (1 required)");
        out.println("  LOAD      is the path to your load configuration, for example /tmp/load.properties, (1 required");
        out.println("  SUMMARY   is the path to your summary configuration, for example /tmp/summary.properties, (optional");
        out.println();
        out.println("Notes:");
        out.println();
        out.println("  Multiple transform configurations are allowed, using the order from left to right");
    }

}
