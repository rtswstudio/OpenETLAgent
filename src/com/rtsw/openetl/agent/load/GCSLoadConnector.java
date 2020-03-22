package com.rtsw.openetl.agent.load;

import com.google.cloud.storage.*;
import com.rtsw.openetl.agent.api.Format;
import com.rtsw.openetl.agent.api.LoadConnector;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author RT Software Studio
 */
public class GCSLoadConnector implements LoadConnector {

    private static final String ID = GCSLoadConnector.class.getName();

    private Report report = new Report(ID);

    private File workDir;

    private String projectId;

    private String bucket;

    private String path;

    private boolean compress = false;

    private boolean delete = true;

    private Map<String, OutputStream> streams;

    private Map<String, String> files;

    private Storage storage;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        workDir = new File(configuration.get("work_dir", null));
        if (workDir == null) {
            throw new Exception("missing required parameter 'work_dir'");
        }
        if (!workDir.exists()) {
            throw new Exception(String.format("work directory '%s' does not exist", workDir.getAbsolutePath()));
        }
        if (!workDir.isDirectory()) {
            throw new Exception(String.format("work directory '%s' is not a directory", workDir.getAbsolutePath()));
        }
        if (!workDir.canWrite()) {
            throw new Exception(String.format("work directory '%s' does not have write permission", workDir.getAbsolutePath()));
        }

        // required
        projectId = configuration.get("project_id", null);
        if (projectId == null) {
            throw new Exception("missing required parameter 'project_id'");
        }

        // required
        bucket = configuration.get("bucket", null);
        if (bucket == null) {
            throw new Exception("missing required parameter 'bucket'");
        }

        // optional
        path = configuration.get("path", "/");
        if (path == null) {
            throw new Exception("missing required parameter 'path'");
        }

        // optional
        compress = configuration.get("compress", false);

        // delete
        delete = configuration.get("delete", true);

        streams = new HashMap<>();
        files = new HashMap<>();

        // storage
        storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    }

    @Override
    public void header(Format format) {
        if (format.getHeader() != null) {
            try {
                OutputStream out = streams.get(format.getHeader());
                out.write(format.getRowSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void rowSeparator(Table table, Format format) {
        if (table == null) {
            return;
        }
        if (format.getRowSeparator() != null) {
            try {
                OutputStream out = streams.get(table.getName());
                out.write(format.getRowSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void headerSeparator(Table table, Format format) {
        if (table == null) {
            return;
        }
        if (format.getHeaderSeparator() != null) {
            try {
                OutputStream out = streams.get(table.getName());
                out.write(format.getHeaderSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void footer(Format format) {
        if (format.getFooter() != null) {
            try {
                OutputStream out = streams.get(format.getFooter());
                out.write(format.getRowSeparator());
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
    }

    @Override
    public void load(Table table, Format format) {
        if (table == null) {
            return;
        }
        OutputStream out = null;
        try {
            String file;
            if (compress) {
                file = table.getName() + "." + format.getFileExtensionHint() + ".gz";
                out = new GZIPOutputStream(new FileOutputStream(new File(workDir, file), false));

            } else {
                file = table.getName() + "." + format.getFileExtensionHint();
                out = new BufferedOutputStream(new FileOutputStream(new File(workDir, file), false));
            }
            out.write(format.format(table));
            streams.put(table.getName(), out);
            files.put(table.getName(), file);
            report.column(table.getColumns().size());
            report.table();
        } catch (Exception e) {
            report.error(e.getMessage());
        }
    }

    @Override
    public void load(Table table, Row row, Format format) {
        if (table == null || row == null) {
            return;
        }
        try {
            OutputStream out = streams.get(table.getName());
            out.write(format.format(table, row));
            report.row();
        } catch (Exception e) {
            report.error(e.getMessage());
        }
    }

    @Override
    public void clean() {
        for (OutputStream out : streams.values()) {
            try {
                out.close();
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
        for (String s : files.values()) {
            try {
                File file = new File(workDir, s);
                BlobId blobId;
                if (path == null || path.isEmpty() || path.equals("/")) {
                    blobId = BlobId.of(bucket, s);
                } else {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    }
                    blobId = BlobId.of(bucket, path + "/" + s);
                }
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.create(blobInfo, Files.readAllBytes(file.toPath()));
                if (delete) {
                    file.delete();
                }
            } catch (Exception e) {
                report.error(e.getMessage());
            }
        }
        report.end();
    }

    @Override
    public Report report() {
        return (report);
    }

}
