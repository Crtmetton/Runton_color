package com.colorrun.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;

public class DatabaseConfig {
    
    private static final String DB_URL = "jdbc:h2:file:./dbFiles/Runton_color_Prod;AUTO_SERVER=TRUE";
    private static final String DB_USER = "Runton";
    private static final String DB_PASSWORD = "";
    
    private static DataSource dataSource;
    
    static {
        try {
            initDataSource();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    private static void initDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        dataSource = ds;
    }
    
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
