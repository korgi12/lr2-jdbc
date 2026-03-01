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

            System.out.println("RMI-сервер запущен. Имя сервиса: DepartmentEmployeeService");
        } catch (Exception e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
