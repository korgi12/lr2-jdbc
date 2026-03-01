package edu.lr2.jdbc.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RmiClientApp {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            DepartmentEmployeeRemote remote = (DepartmentEmployeeRemote) registry.lookup("DepartmentEmployeeService");

            boolean running = true;
            while (running) {
                printMenu();
                String command = scanner.nextLine().trim();

                if ("1".equals(command)) {
                    System.out.println(remote.getEmployeesWithDepartments());
                } else if ("2".equals(command)) {
                    System.out.print("department_id: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("department_name: ");
                    String name = scanner.nextLine();
                    System.out.print("building: ");
                    String building = scanner.nextLine();
                    System.out.println(remote.addDepartment(id, name, building));
                } else if ("3".equals(command)) {
                    System.out.print("employee_id: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("full_name: ");
                    String fullName = scanner.nextLine();
                    System.out.print("position: ");
                    String position = scanner.nextLine();
                    System.out.print("department_id: ");
                    int departmentId = Integer.parseInt(scanner.nextLine());
                    System.out.println(remote.addEmployee(id, fullName, position, departmentId));
                } else if ("4".equals(command)) {
                    System.out.print("department_id to delete: ");
                    int departmentId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Delete linked employees too? (yes/no): ");
                    boolean cascade = "yes".equalsIgnoreCase(scanner.nextLine().trim());
                    System.out.println(remote.deleteDepartmentById(departmentId, cascade));
                } else if ("5".equals(command)) {
                    System.out.print("employee_id to delete: ");
                    int employeeId = Integer.parseInt(scanner.nextLine());
                    System.out.println(remote.deleteEmployeeById(employeeId));
                } else if ("0".equals(command)) {
                    running = false;
                } else {
                    System.out.println("Unknown command.");
                }
            }
        } catch (Exception e) {
            System.err.println("Client failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Department/Employee RMI Client ---");
        System.out.println("1 - Show employees with departments");
        System.out.println("2 - Add department");
        System.out.println("3 - Add employee");
        System.out.println("4 - Delete department by id");
        System.out.println("5 - Delete employee by id");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }
}
