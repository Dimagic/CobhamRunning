package net.ddns.dimag.cobhamrunning.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

public class ConnectionInfo implements Work {

    public String dataBaseUrl;
    public String dataBaseProductName;
    public String driverName;
    public String username;

    @Override
    public void execute(Connection connection) throws SQLException {
        dataBaseUrl = connection.getMetaData().getURL();
        dataBaseProductName = connection.getMetaData().getDatabaseProductName();
        driverName = connection.getMetaData().getDriverName();
        username = connection.getMetaData().getUserName();

    }

    public String getDataBaseProductName() {
        return dataBaseProductName;
    }

    public void setDataBaseProductName(String dataBaseProductName) {
        this.dataBaseProductName = dataBaseProductName;
    }

    public String getDataBaseUrl() {
        return dataBaseUrl;
    }

    public void setDataBaseUrl(String dataBaseUrl) {
        this.dataBaseUrl = dataBaseUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
