package com.rtsw.openetl.agent.load;

import com.rtsw.openetl.agent.api.*;
import com.rtsw.openetl.agent.common.Configuration;
import com.rtsw.openetl.agent.common.Report;
import com.rtsw.openetl.agent.common.Row;
import com.rtsw.openetl.agent.common.Table;

import java.sql.*;

/**
 * @author RT Software Studio
 */
public class JDBCLoadConnector implements LoadConnector {

    private static final String ID = JDBCLoadConnector.class.getName();

    private Report report = new Report(ID);

    private String className;

    private String url;

    private String username;

    private String password;

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

    }

    @Override
    public void header(Format format) {
    }

    @Override
    public void headerSeparator(Table table, Format format) {
    }

    @Override
    public void rowSeparator(Table table, Format format) {
    }

    @Override
    public void footer(Format format) {
    }

    @Override
    public void load(Table table, Format format) {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(new String(format.format(table)));
            statement.execute();
            report.column(table.getColumns().size());
            report.table();
        } catch (Exception e) {
            report.error(e.getMessage());
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void load(Table table, Row row, Format format) {
        Connection connection = null;
        try {
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute(new String(format.format(table, row)));
            report.row();
        } catch (Exception e) {
            report.error(e.getMessage());
        } finally {
            closeConnection(connection);
        }
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

}
