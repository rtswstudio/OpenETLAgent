package com.rtsw.openetl.agent.extract;

import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.api.ExtractConnector;
import com.rtsw.openetl.agent.common.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class URLExtractConnector implements ExtractConnector {

    private static final String ID = URLExtractConnector.class.getName();

    private Report report = new Report(ID);

    private String url;

    private int connectTimeout;

    private int readTimeout;

    private String proxy;

    private String encoding;

    private String userAgent;

    private String tablePattern;

    private AgentListener agentListener;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        url = configuration.get("url", null);
        if (url == null) {
            throw new Exception("missing required parameter 'url'");
        }

        // optional
        connectTimeout = configuration.get("connect_timeout", 10 * 1000);

        // optional
        readTimeout = configuration.get("read_timeout", 30 * 1000);

        // optional
        proxy = configuration.get("proxy", null);

        // optional
        encoding = configuration.get("encoding", "UTF-8");

        // optional
        userAgent = configuration.get("user_agent", "openetl-agent");

        // optional
        tablePattern = configuration.get("table_pattern", null);

    }

    @Override
    public void extract(AgentListener agentListener) {

        // event listener
        this.agentListener = agentListener;

        agentListener.onStart();

        try {
            URL url = new URL(this.url);
            Proxy proxy = Proxy.NO_PROXY;
            if (this.proxy != null && !this.proxy.isEmpty()) {
                URL proxyUrl = new URL(this.proxy);
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept", "text/html");
            connection.setRequestProperty("Host", url.getHost());
            connection.setRequestProperty("Date", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z").format(new Date()));
            connection.setRequestProperty("User-Agent", userAgent);
            Document document = Jsoup.parse(connection.getInputStream(), encoding, this.url);
            Elements tables = document.select("table");
            int i = 0;
            for (Element table : tables) {
                String tableName = "" + i;
                if (table.attr("name") != null) {
                    tableName = table.attr("name");
                }
                if (table.attr("id") != null) {
                    tableName = table.attr("id");
                }
                if (!tableName.matches(tablePattern)) {
                    continue;
                }
                Table t = new Table();
                t.setName(tableName);
                Elements trs = table.select("tr");
                int j = 0;
                for (Element tr : trs) {
                    // head
                    Elements ths = tr.select("th");
                    if (ths != null && ths.size() > 0) {
                        List<Column> columns = new ArrayList<>();
                        for (Element th : ths) {
                            columns.add(new Column(th.text(), "String", "java.lang.String"));
                        }
                        t.setColumns(columns);
                        agentListener.onTable(t);
                        report.column(columns.size());
                    }

                    // row
                    Elements tds = tr.select("td");
                    if (tds != null && tds.size() > 0) {
                        // check if we got head previously
                        if (j == 0 && t.getColumns() == null) {
                            List<Column> columns = new ArrayList<>();
                            for (Element td : tds) {
                                columns.add(new Column(td.text(), "String", "java.lang.String"));
                            }
                            t.setColumns(columns);
                            agentListener.onTable(t);
                            report.column(columns.size());
                        } else {
                            List<Object> items = new ArrayList<>();
                            for (Element td : tds) {
                                items.add(td.text());
                            }
                            Row r = new Row(items);
                            agentListener.onRow(t, r);
                            report.row();
                        }
                    }
                    j++;
                }
                report.table();
                i++;
            }

        } catch (Exception e) {
            report.error(e.getMessage());
        }

        agentListener.onEnd();

    }

    @Override
    public void clean() {
        report.end();
    }

    @Override
    public Report report() {
        return (report);
    }
}
