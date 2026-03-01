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
                    System.out.print("Идентификатор отдела: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Название отдела: ");
                    String name = scanner.nextLine();
                    System.out.print("Здание: ");
                    String building = scanner.nextLine();
                    System.out.println(remote.addDepartment(id, name, building));
                } else if ("3".equals(command)) {
                    System.out.print("Идентификатор сотрудника: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("ФИО: ");
                    String fullName = scanner.nextLine();
                    System.out.print("Должность: ");
                    String position = scanner.nextLine();
                    System.out.print("Идентификатор отдела: ");
                    int departmentId = Integer.parseInt(scanner.nextLine());
                    System.out.println(remote.addEmployee(id, fullName, position, departmentId));
                } else if ("4".equals(command)) {
                    System.out.print("Идентификатор отдела для удаления: ");
                    int departmentId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Удалить связанных сотрудников? (да/нет): ");
                    boolean cascade = "да".equalsIgnoreCase(scanner.nextLine().trim());
                    System.out.println(remote.deleteDepartmentById(departmentId, cascade));
                } else if ("5".equals(command)) {
                    System.out.print("Идентификатор сотрудника для удаления: ");
                    int employeeId = Integer.parseInt(scanner.nextLine());
                    System.out.println(remote.deleteEmployeeById(employeeId));
                } else if ("0".equals(command)) {
                    running = false;
                } else {
                    System.out.println("Неизвестная команда.");
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\n--- RMI-клиент отделов и сотрудников ---");
        System.out.println("1 - Показать сотрудников с отделами");
        System.out.println("2 - Добавить отдел");
        System.out.println("3 - Добавить сотрудника");
        System.out.println("4 - Удалить отдел по идентификатору");
        System.out.println("5 - Удалить сотрудника по идентификатору");
        System.out.println("0 - Выход");
        System.out.print("Выберите пункт: ");
    }
}
