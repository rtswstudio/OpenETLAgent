package com.rtsw.openetl.agent.extract;

import com.healthmarketscience.jackcess.DataType;
import com.rtsw.openetl.agent.common.Column;
import com.rtsw.openetl.agent.api.ExtractConnector;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.common.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author RT Software Studio
 */
public class JDBCExtractConnector implements ExtractConnector {

    private static final String ID = JDBCExtractConnector.class.getName();

    private Report report = new Report(ID);

    private String className;

    private String url;

    private String username;

    private String password;

    private String tablePattern;

    private int batchSize;

    private AgentListener agentListener;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        className = configuration.get("class_name", null);
        if (className == null) {
            throw new Exception(("missing required parameter 'class_name'"));
        }

        // required
        url = configuration.get("url", null);
        if (url == null) {
            throw new Exception("missing required parameter 'url'");
        }

        // optional
        username = configuration.get("username", null);

        // optional
        password = configuration.get("password", null);

        // optional
        tablePattern = configuration.get("table_pattern", null);

        // optional
        batchSize = configuration.get("batch_size", 10000);

    }

    @Override
    public void extract(AgentListener agentListener) {

        // event listener
        this.agentListener = agentListener;

        // tables
        Set<String> tableNames = getTableNames();
        if (tableNames == null) {
            return;
        }

        agentListener.onStart();

        // data
        for (String tableName : tableNames) {
            Table table = doTable(tableName);
            agentListener.onTable(table);
            doRow(table);
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

    private Connection getConnection() throws Exception {
        Class.forName(className);
        Connection connection;
        if (username == null || password == null) {
            connection = DriverManager.getConnection(url);
        } else {
            connection = DriverManager.getConnection(url, username, password);
        }
        return (connection);
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                report.warning(e.getMessage());
            }
        }
    }

    private Set<String> getTableNames() {
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet set = metaData.getTables(null, null, "%", null);
            Set<String> tableNames = new HashSet<>();
            while (set != null && set.next()) {
                String tableName = set.getString(3);
                if (tablePattern != null && !tableName.matches(tablePattern)) {
                    continue;
                }
                tableNames.add(tableName);
            }
            set.close();
            return (tableNames);
        } catch (Exception e) {
            report.error(e.getMessage());
            return (null);
        } finally {
            closeConnection(connection);
        }
    }

    private Table doTable(String tableName) {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + tableName + "` LIMIT 1");
            ResultSet set = statement.executeQuery();
            ResultSetMetaData metaData = set.getMetaData();
            if (metaData != null) {
                List<Column> columns = new ArrayList<>();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    columns.add(getColumn(metaData.getColumnName(i), metaData.getColumnType(i)));
                }
                Table table = new Table(tableName, columns);
                report.table();
                return (table);
            } else {
                return (null);
            }
        } catch (Exception e) {
            report.error(e.getMessage());
            return (null);
        } finally {
            closeConnection(connection);
        }
    }

    private void doRow(Table table) {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = null;
            ResultSet set = null;
            long offset = 0;
            long limit = batchSize;
            boolean rows = true;
            while (rows) {
                statement = connection.prepareStatement("SELECT * FROM `" + table.getName() + "` LIMIT " + offset + ", " + limit);
                set = statement.executeQuery();
                if (set != null && set.next()) {
                    do {
                        List<Object> values = new ArrayList<>();
                        for (int i = 1; i < set.getMetaData().getColumnCount() + 1; i++) {
                            values.add(set.getObject(i));
                            report.column();
                        }
                        Row row = new Row(values);
                        report.row();
                        agentListener.onRow(table, row);
                    } while (set.next());
                    offset += limit;
                } else {
                    rows = false;
                }
            }
            set.close();
            statement.close();
        } catch (Exception e) {
            report.error(e.getMessage());
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Map SQL type to Java type
     *
     * https://www.service-architecture.com/articles/database/mapping_sql_and_java_data_types.html
     *
     * @param name
     * @param type
     * @return
     */
    private Column getColumn(String name, int type) {
        Column column = new Column();
        column.setName(name);
        column.setTypeName("Object");
        column.setClassName("java.lang.Object");

        // string
        if (type == Types.VARCHAR || type == Types.CHAR) {
            column.setTypeName("String");
            column.setClassName("java.lang.String");
        }

        // big decimal
        if (type == Types.DECIMAL || type == Types.NUMERIC) {
            column.setTypeName("BigDecimal");
            column.setClassName("java.math.BigDecimal");
        }

        // boolean
        if (type == Types.BOOLEAN || type == Types.BIT) {
            column.setTypeName("Boolean");
            column.setClassName("java.lang.Boolean");
        }

        // integer
        if (type == Types.INTEGER || type == Types.SMALLINT || type == Types.TINYINT) {
            column.setTypeName("Integer");
            column.setClassName("java.lang.Integer");
        }

        // long
        if (type == Types.BIGINT) {
            column.setTypeName("Long");
            column.setClassName("java.lang.Long");
        }

        // float
        if (type == Types.REAL) {
            column.setTypeName("Float");
            column.setClassName("java.lang.Float");
        }

        // double
        if (type == Types.DOUBLE || type == Types.FLOAT) {
            column.setTypeName("Double");
            column.setClassName("java.lang.Double");
        }

        // date
        if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE || type == Types.TIME) {
            column.setTypeName("Date");
            column.setClassName("java.util.Date");
        }

        return (column);
    }

}
