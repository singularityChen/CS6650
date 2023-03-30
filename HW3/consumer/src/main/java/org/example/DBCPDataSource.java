package org.example;

import org.apache.commons.dbcp2.*;

public class DBCPDataSource {
    private static BasicDataSource dataSource;

    // NEVER store sensitive information below in plain text!
//    private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
//    private static final String PORT = System.getProperty("MySQL_PORT");
//    private static final String DATABASE = "LiftRides";
//    private static final String USERNAME = System.getProperty("DB_USERNAME");
//    private static final String PASSWORD = System.getProperty("DB_PASSWORD");
    private static final String HOST_NAME = "database-2.chgsebnp8xxm.us-west-2.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "sys";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "19980207";

    static {
        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
        dataSource = new BasicDataSource();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("db.t3.micro");
            //db.t3.micro
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(60);
    }

    public static BasicDataSource getDataSource() {
        return dataSource;
    }
}
