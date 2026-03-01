package edu.lr2.jdbc.rmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDepartmentEmployeeService {

    public String getEmployeesWithDepartments() throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT e.employee_id, e.full_name, e.position, d.department_name, d.building "
                + "FROM employees e "
                + "JOIN departments d ON e.department_id = d.department_id "
                + "ORDER BY e.employee_id";

        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                result.append("Employee #").append(rs.getInt("employee_id"))
                        .append(" | name=").append(rs.getString("full_name"))
                        .append(" | position=").append(rs.getString("position"))
                        .append(" | department=").append(rs.getString("department_name"))
                        .append(" | building=").append(rs.getString("building"))
                        .append('\n');
            }
        }
        return result.length() == 0 ? "No employees found." : result.toString();
    }

    public String addDepartment(int departmentId, String departmentName, String building) throws SQLException {
        String sql = "INSERT INTO departments(department_id, department_name, building) VALUES (%d, '%s', '%s')";
        String query = String.format(sql, departmentId, escape(departmentName), escape(building));

        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL);
             Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(query);
            return affectedRows == 1 ? "Department added." : "Department was not added.";
        }
    }

    public String addEmployee(int employeeId, String fullName, String position, int departmentId) throws SQLException {
        String sql = "INSERT INTO employees(employee_id, full_name, position, department_id) VALUES (%d, '%s', '%s', %d)";
        String query = String.format(sql, employeeId, escape(fullName), escape(position), departmentId);

        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("PRAGMA foreign_keys = ON");
            int affectedRows = statement.executeUpdate(query);
            return affectedRows == 1 ? "Employee added." : "Employee was not added.";
        }
    }

    public String deleteDepartmentById(int departmentId, boolean deleteLinkedEmployees) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL)) {
            connection.createStatement().executeUpdate("PRAGMA foreign_keys = ON");

            int linkedEmployees = countEmployeesByDepartment(connection, departmentId);
            if (linkedEmployees > 0 && !deleteLinkedEmployees) {
                return "Department has " + linkedEmployees
                        + " linked employees. Request denied. Repeat with deleteLinkedEmployees=true to remove linked employees first.";
            }

            if (linkedEmployees > 0) {
                try (PreparedStatement deleteEmployees = connection.prepareStatement(
                        "DELETE FROM employees WHERE department_id = ?")) {
                    deleteEmployees.setInt(1, departmentId);
                    deleteEmployees.executeUpdate();
                }
            }

            try (PreparedStatement deleteDepartment = connection.prepareStatement(
                    "DELETE FROM departments WHERE department_id = ?")) {
                deleteDepartment.setInt(1, departmentId);
                int affectedRows = deleteDepartment.executeUpdate();
                return affectedRows == 1 ? "Department deleted." : "Department not found.";
            }
        }
    }

    public String deleteEmployeeById(int employeeId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM employees WHERE employee_id = ?")) {

            preparedStatement.setInt(1, employeeId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 1 ? "Employee deleted." : "Employee not found.";
        }
    }

    private int countEmployeesByDepartment(Connection connection, int departmentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM employees WHERE department_id = ?")) {
            statement.setInt(1, departmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    private String escape(String value) {
        return value.replace("'", "''");
    }
}
