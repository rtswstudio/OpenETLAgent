package com.rtsw.openetl.agent.load;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.rtsw.openetl.agent.api.Format;
import com.rtsw.openetl.agent.api.LoadConnector;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author RT Software Studio
 */
public class AzureLoadConnector implements LoadConnector {

    private static final String ID = AzureLoadConnector.class.getName();

    private Report report = new Report(ID);

    private File workDir;

    private String storageAccountUrl;

    private String sasToken;

    private String container;

    private String path;

    private boolean overwrite = true;

    private boolean compress = false;

    private boolean delete = true;

    private Map<String, OutputStream> streams;

    private Map<String, String> files;

    private BlobContainerClient blobContainerClient;

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
        storageAccountUrl = configuration.get("storage_account_url", null);
        if (storageAccountUrl == null) {
            throw new Exception("missing required parameter 'storage_account_url'");
        }

        // required
        sasToken = configuration.get("sas_token", null);
        if (sasToken == null) {
            throw new Exception("missing required parameter 'sas_token'");
        }

        // required
        container = configuration.get("container", null);
        if (container == null) {
            throw new Exception("missing required parameter 'container'");
        }

        // required
        path = configuration.get("path", null);
        if (path == null) {
            throw new Exception("missing required parameter 'path'");
        }

        // optional
        overwrite = configuration.get("overwrite", true);

        // optional
        compress = configuration.get("compress", false);

        // delete
        delete = configuration.get("delete", true);

        streams = new HashMap<>();
        files = new HashMap<>();

        // storage?
        blobContainerClient = new BlobContainerClientBuilder().endpoint(storageAccountUrl).sasToken(sasToken).containerName(container).buildClient();

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
                BlobClient blobClient = blobContainerClient.getBlobClient(path + "/" + s);
                blobClient.uploadFromFile(file.getAbsolutePath(), overwrite);
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
