package com.globus.dadataadapter.logging;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory class for creating database connections.
 *
 * <p>Provides a centralized way to obtain database connections for logger.</p>
 *
 * @author [Okhrimenko Alexey]
 */
@Slf4j
public class CustomConnectionFactory {
    private static final String URL = "jdbc:postgresql://localhost:5428/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    /**
     * Creates and returns a database connection using predefined credentials.
     *
     * @return A new Connection object to the database
     * @throws SQLException If a database access error occurs or connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}