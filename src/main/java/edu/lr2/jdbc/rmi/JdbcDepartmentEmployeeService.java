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

        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.JDBC_USER, DatabaseConfig.JDBC_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                result.append("Сотрудник №").append(rs.getInt("employee_id"))
                        .append(" | ФИО=").append(rs.getString("full_name"))
                        .append(" | должность=").append(rs.getString("position"))
                        .append(" | отдел=").append(rs.getString("department_name"))
                        .append(" | здание=").append(rs.getString("building"))
                        .append('\n');
            }
        }
        return result.length() == 0 ? "Сотрудники не найдены." : result.toString();
    }

    public String addDepartment(int departmentId, String departmentName, String building) throws SQLException {
        String sql = "INSERT INTO departments(department_id, department_name, building) VALUES (%d, '%s', '%s')";
        String query = String.format(sql, departmentId, escape(departmentName), escape(building));

        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.JDBC_USER, DatabaseConfig.JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(query);
            return affectedRows == 1 ? "Отдел добавлен." : "Отдел не был добавлен.";
        }
    }

    public String addEmployee(int employeeId, String fullName, String position, int departmentId) throws SQLException {
        String sql = "INSERT INTO employees(employee_id, full_name, position, department_id) VALUES (%d, '%s', '%s', %d)";
        String query = String.format(sql, employeeId, escape(fullName), escape(position), departmentId);

        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.JDBC_USER, DatabaseConfig.JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(query);
            return affectedRows == 1 ? "Сотрудник добавлен." : "Сотрудник не был добавлен.";
        }
    }

    public String deleteDepartmentById(int departmentId, boolean deleteLinkedEmployees) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.JDBC_USER, DatabaseConfig.JDBC_PASSWORD)) {
            int linkedEmployees = countEmployeesByDepartment(connection, departmentId);
            if (linkedEmployees > 0 && !deleteLinkedEmployees) {
                return "В отделе " + linkedEmployees
                        + " связанных сотрудников. Запрос отклонён. Повторите с deleteLinkedEmployees=true, чтобы сначала удалить связанных сотрудников.";
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
                return affectedRows == 1 ? "Отдел удалён." : "Отдел не найден.";
            }
        }
    }

    public String deleteEmployeeById(int employeeId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.JDBC_USER, DatabaseConfig.JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM employees WHERE employee_id = ?")) {

            preparedStatement.setInt(1, employeeId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 1 ? "Сотрудник удалён." : "Сотрудник не найден.";
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
