# Лабораторная работа №2: JDBC + RMI

Проект реализует требования лабораторной работы по дисциплине
«Объектная распределенная обработка»:

- JDBC-взаимодействие Java-приложения с БД.
- Использование двух связанных таблиц.
- Сервер/клиент на Java RMI, где удаленный объект предоставляет JDBC-методы.

## Предметная область

Используются таблицы:

1. `departments` (главная таблица)
   - `department_id` (PK)
   - `department_name`
   - `building`
2. `employees` (подчиненная таблица)
   - `employee_id` (PK)
   - `full_name`
   - `position`
   - `department_id` (FK -> `departments.department_id`)

## Что реализовано

Класс `JdbcDepartmentEmployeeService` содержит методы:

- вывод списка сотрудников с присоединением департамента (JOIN);
- добавление записи в главную таблицу `departments`;
- добавление записи в `employees` с указанием внешнего ключа `department_id`;
- удаление записи из `departments` по PK (через `PreparedStatement`) с обязательной проверкой связанных записей;
- удаление записи из `employees` по PK (через `PreparedStatement`).

Для первых трех пунктов используется `Statement` + обычные SQL-запросы.
Для удаления используется параметризованный запрос `PreparedStatement`.

## Структура RMI

- `DepartmentEmployeeRemote` — remote-интерфейс.
- `DepartmentEmployeeRemoteImpl` — удаленный объект (делегирует JDBC-сервису).
- `RmiServerApp` — сервер, создаёт/регистрирует объект в RMI Registry (`DepartmentEmployeeService`).
- `RmiClientApp` — консольный клиент с меню, вызывает удаленные методы.

## JDBC драйвер

В `pom.xml` подключен драйвер SQLite:

- `org.xerial:sqlite-jdbc:3.46.1.3`

БД хранится в файле `lab2.db`, URL подключения:

- `jdbc:sqlite:lab2.db`

## Запуск

```bash
mvn clean package
```

### 1) Запуск сервера

```bash
mvn -q exec:java -Dexec.mainClass=edu.lr2.jdbc.rmi.RmiServerApp
```

### 2) Запуск клиента

В другом терминале:

```bash
mvn -q exec:java -Dexec.mainClass=edu.lr2.jdbc.rmi.RmiClientApp
```

> При первом старте сервер автоматически создает таблицы, если их нет.
