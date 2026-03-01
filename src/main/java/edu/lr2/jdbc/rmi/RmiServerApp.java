package edu.lr2.jdbc.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServerApp {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
            JdbcDepartmentEmployeeService service = new JdbcDepartmentEmployeeService();
            DepartmentEmployeeRemote remoteObject = new DepartmentEmployeeRemoteImpl(service);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("DepartmentEmployeeService", remoteObject);

            System.out.println("RMI server started. Service name: DepartmentEmployeeService");
        } catch (Exception e) {
            System.err.println("Server startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
