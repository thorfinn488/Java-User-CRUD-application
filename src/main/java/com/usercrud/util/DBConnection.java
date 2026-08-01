package com.usercrud.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection Utility Class
 *
 * Centralizes all JDBC connection details in ONE place.
 * Every DAO method calls DBConnection.getConnection() instead of
 * hard-coding connection strings, which makes the app easy to
 * reconfigure (e.g. changing DB host/credentials) without touching
 * business logic.
 */
public class DBConnection {

    // ---- Database configuration ----
    // NOTE: In a real production app these values should come from
    // an external config file, environment variables, or a
    // connection pool (e.g. HikariCP) instead of being hard-coded.
    private static final String URL =
            "jdbc:mysql://localhost:3306/user_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "ayush2005"; // change to your MySQL password

    // Load the MySQL JDBC driver once, when this class is first loaded.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // If the driver JAR is missing from the classpath, fail loudly and early.
            throw new RuntimeException("MySQL JDBC Driver not found. Check your pom.xml dependency.", e);
        }
    }

    /**
     * Private constructor prevents instantiation -
     * this class only exposes a static factory method.
     */
    private DBConnection() {
    }

    /**
     * Opens and returns a new JDBC Connection to the MySQL database.
     * Callers are responsible for closing the connection
     * (preferably using try-with-resources).
     *
     * @return an open java.sql.Connection
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
