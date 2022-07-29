
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.dao;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import static java.util.concurrent.TimeUnit.*;

/**
 * Database Connection class establishes connection to the MySQL Database
 * Non-instantiable utility class
 * @author manue
 */
public class DBConnection {

    // private constructor for non-instantiable utility class
    private DBConnection() {
        throw new AssertionError();
    }

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream propFile = new FileInputStream("src/main/resources/org/manuel/dbConnection.properties");
            properties.load(propFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String PROTOCOL = properties.getProperty("protocol");
    private static final String VENDOR_NAME = properties.getProperty("vendorName");
    private static final String IP_ADDRESS = properties.getProperty("ipAddress");
    private static final String DB_NAME = properties.getProperty("dbName");
    private static final String DB_SETTING = properties.getProperty("dbSetting"); //v8.0.23
    private static final String jdbcURL = PROTOCOL + VENDOR_NAME + IP_ADDRESS + DB_NAME + DB_SETTING;
    private static final String COM_MYSQL_CJ_JDBC_DRIVER = properties.getProperty("mysqlJDBCDriver");
    private static final String USER_NAME = properties.getProperty("dbUser");
    private static final String PASSWORD = properties.getProperty("dbPassword");
    private static Connection conn = null;

    /**
     * Starts connection to SQL Database
     * @return Connection
     */
    public static Connection startConnection() {
        try {
            Class.forName(COM_MYSQL_CJ_JDBC_DRIVER);

            conn = DriverManager.getConnection(jdbcURL, USER_NAME, PASSWORD);
            System.out.println("Connection Successful");
        }

        catch (SQLException | ClassNotFoundException e){
            e.printStackTrace(System.out);
        }

        return conn;
    }

    /**
     * Gets connection to SQL database
     * @return Connection
     */
    public static Connection getConnection(){
        return conn;
    }

    /**
     *Closes SQL connection to database
     */
    public static void closeConnection(){

        try {
            conn.close();
        }

        catch (Exception e) {
            // DO NOTHING
        }
    }

    @Override
    public String toString(){
        return DB_NAME;

    }
}

