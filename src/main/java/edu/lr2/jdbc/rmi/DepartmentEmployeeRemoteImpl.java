package edu.lr2.jdbc.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class DepartmentEmployeeRemoteImpl extends UnicastRemoteObject implements DepartmentEmployeeRemote {
    private final JdbcDepartmentEmployeeService service;

    public DepartmentEmployeeRemoteImpl(JdbcDepartmentEmployeeService service) throws RemoteException {
        this.service = service;
    }

    public String getEmployeesWithDepartments() throws RemoteException {
        try {
            return service.getEmployeesWithDepartments();
        } catch (SQLException e) {
            throw new RemoteException("Cannot fetch employee list.", e);
        }
    }

    public String addDepartment(int departmentId, String departmentName, String building) throws RemoteException {
        try {
            return service.addDepartment(departmentId, departmentName, building);
        } catch (SQLException e) {
            throw new RemoteException("Cannot add department.", e);
        }
    }

    public String addEmployee(int employeeId, String fullName, String position, int departmentId) throws RemoteException {
        try {
            return service.addEmployee(employeeId, fullName, position, departmentId);
        } catch (SQLException e) {
            throw new RemoteException("Cannot add employee.", e);
        }
    }

    public String deleteDepartmentById(int departmentId, boolean deleteLinkedEmployees) throws RemoteException {
        try {
            return service.deleteDepartmentById(departmentId, deleteLinkedEmployees);
        } catch (SQLException e) {
            throw new RemoteException("Cannot delete department.", e);
        }
    }

    public String deleteEmployeeById(int employeeId) throws RemoteException {
        try {
            return service.deleteEmployeeById(employeeId);
        } catch (SQLException e) {
            throw new RemoteException("Cannot delete employee.", e);
        }
    }
}
