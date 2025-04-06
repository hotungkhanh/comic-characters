package com.tuka.comiccharacters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "URL";
    private static final String USER = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = connect()) {
            System.out.println("Connected to PostgreSQL!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
