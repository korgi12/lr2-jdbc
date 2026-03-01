package edu.lr2.jdbc.rmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("PRAGMA foreign_keys = ON");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS departments ("
                    + "department_id INTEGER PRIMARY KEY, "
                    + "department_name TEXT NOT NULL, "
                    + "building TEXT NOT NULL"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS employees ("
                    + "employee_id INTEGER PRIMARY KEY, "
                    + "full_name TEXT NOT NULL, "
                    + "position TEXT NOT NULL, "
                    + "department_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (department_id) REFERENCES departments(department_id)"
                    + ")");
        }
    }
}
