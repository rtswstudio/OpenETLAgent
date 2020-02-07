package com.rtsw.openetl.agent.summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtsw.openetl.agent.api.SummaryPusher;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Summary;
import org.apache.tools.ant.taskdefs.condition.Http;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

/**
 * @author RT Software Studio
 */
public class PortalSummaryPusher implements SummaryPusher {

    private ObjectMapper mapper = new ObjectMapper();

    private String organization;

    private String apiKey;

    private String title;

    private String description;

    private String url;

    private int connectTimeout;

    private int readTimeout;

    private String encoding;

    private String proxy;

    @Override
    public void init(Configuration configuration) throws Exception {

        // required
        organization = configuration.get("organization", null);

        // required
        apiKey = configuration.get("apikey", null);

        // required
        title = configuration.get("title", null);

        // optional
        description = configuration.get("description", null);

        // optional
        url = configuration.get("url", "https://openetl-portal.appspot.com/api/v1/summary");

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
        summary.setTitle(title);
        summary.setDescription(description);
        BufferedOutputStream out = null;
        try {
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z").format(new Date());
            String content =mapper.writeValueAsString(summary);
            URL url = new URL(this.url + "?oid=" + organization);
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
            connection.setRequestProperty("Content-Length", "" + content.length());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Date", date);
            connection.setInstanceFollowRedirects(true);
            StringBuilder token = new StringBuilder();
            token.append(url.getHost());
            token.append("|");
            token.append("post");
            token.append("|");
            token.append(url.getPath());
            token.append("|");
            token.append(url.getQuery());
            token.append("|");
            token.append(date);
            connection.setRequestProperty("X-OpenETL-Portal-Token", HmacSHA256(apiKey, token.toString()));
            out = new BufferedOutputStream(connection.getOutputStream());
            out.write(content.getBytes(encoding));
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

    private static String HmacSHA256(String secretKey, String stringToSign) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signed = mac.doFinal(stringToSign.getBytes());
            StringBuffer hex = new StringBuffer();
            for (int i = 0; i < signed.length; i++) {
                String c = Integer.toHexString(0xFF & signed[i]);
                if (c.length() == 1) {
                    hex.append("0");
                }
                hex.append(c);
            }
            return (hex.toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return (null);
        }
    }

}