package uk.ac.open.kmi.uciad.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * The Class FeedItemDBConnection is used to connect to the MySQL Database.
 */
public class DBConnection {
    /**
     * Creates a Connection object.
     * @return Connection
     */
    protected static Connection getConnection(String dbHost, String dbName, String dbUsername, String dbPassword) {
        
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://"+dbHost+"/"+dbName;
            con = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return con;
    }
}