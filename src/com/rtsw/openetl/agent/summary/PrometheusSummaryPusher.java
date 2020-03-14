package com.rtsw.openetl.agent.summary;

import com.rtsw.openetl.agent.api.SummaryPusher;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Summary;

import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author RT Software Studio
 */
public class PrometheusSummaryPusher implements SummaryPusher {

    private String protocol;

    private String host;

    private int port;

    private String job;

    private String instance;

    private int connectTimeout;

    private int readTimeout;

    private String encoding;

    private String proxy;

    @Override
    public void init(Configuration configuration) throws Exception {

        // required
        host = configuration.get("host", null);

        // optional
        protocol = configuration.get("protocol", "http");

        // optional
        port = configuration.get("port", 9091);

        // optional
        job = configuration.get("job", "openetl");

        // optional
        instance = configuration.get("instance", null);

        // optional
        connectTimeout = configuration.get("connect_timeout", 10 * 1000);

        // optional
        readTimeout = configuration.get("read_timeout", 30 * 1000);

        // optional
        encoding = configuration.get("encoding", "UTF-8");

        // optional
        proxy = configuration.get("proxy", null);

    }

    @Override
    public void push(Summary summary) {
        BufferedOutputStream out = null;
        try {
            String content = formatContent(summary);
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z").format(new Date());
            URL url = null;
            if (instance == null) {
                url = new URL(String.format("%s://%s:%d/metrics/job/%s", protocol, host, port, job));
            } else {
                url = new URL(String.format("%s://%s:%d/metrics/job/%s/instance/%s", protocol, host, port, job, instance));
            }
            Proxy proxy = Proxy.NO_PROXY;
            if (this.proxy != null && !this.proxy.isEmpty()) {
                URL proxyUrl = new URL(this.proxy);
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Length", "" + content.toString().length());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Date", date);
            connection.setInstanceFollowRedirects(true);
            out = new BufferedOutputStream(connection.getOutputStream());
            out.write(content.toString().getBytes(encoding));
            out.flush();
            int status = connection.getResponseCode();
            System.out.println(status);
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

    private String formatContent(Summary summary) {
        StringBuilder sb = new StringBuilder();
        for (Report report : summary.getReports()) {
            sb.append(String.format("openetl_report_duration_milliseconds{implementation=\"%s\"} %d", report.getImplementation(), (report.getEnd().getTime() - report.getStart().getTime())));
            sb.append("\n");
            sb.append(String.format("openetl_report_tables_total{implementation=\"%s\"} %d", report.getImplementation(), report.getTables()));
            sb.append("\n");
            sb.append(String.format("openetl_report_columns_total{implementation=\"%s\"} %d", report.getImplementation(), report.getColumns()));
            sb.append("\n");
            sb.append(String.format("openetl_report_rows_total{implementation=\"%s\"} %d", report.getImplementation(), report.getRows()));
            sb.append("\n");
            sb.append(String.format("openetl_report_extras_total{implementation=\"%s\"} %d", report.getImplementation(), report.getExtras().size()));
            sb.append("\n");
            sb.append(String.format("openetl_report_warnings_total{implementation=\"%s\"} %d", report.getImplementation(), report.getWarnings().size()));
            sb.append("\n");
            sb.append(String.format("openetl_report_errors_total{implementation=\"%s\"} %d", report.getImplementation(), report.getErrors().size()));
            sb.append("\n");
        }
        System.out.println(sb.toString());
        return (sb.toString());
    }

}