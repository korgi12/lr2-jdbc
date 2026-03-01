package edu.lr2.jdbc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DepartmentEmployeeRemote extends Remote {
    String getEmployeesWithDepartments() throws RemoteException;

    String addDepartment(int departmentId, String departmentName, String building) throws RemoteException;

    String addEmployee(int employeeId, String fullName, String position, int departmentId) throws RemoteException;

    String deleteDepartmentById(int departmentId, boolean deleteLinkedEmployees) throws RemoteException;

    String deleteEmployeeById(int employeeId) throws RemoteException;
}
