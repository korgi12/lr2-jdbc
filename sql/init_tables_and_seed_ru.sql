-- Инициализация схемы и тестовых данных (PostgreSQL)
-- Все данные — на русском языке.

BEGIN;

-- На случай повторного запуска скрипта
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;

CREATE TABLE departments (
    department_id INTEGER PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL,
    building VARCHAR(255) NOT NULL
);

CREATE TABLE employees (
    employee_id INTEGER PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    department_id INTEGER NOT NULL,
    CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);

INSERT INTO departments (department_id, department_name, building) VALUES
    (1, 'Отдел разработки', 'Главный корпус'),
    (2, 'Отдел тестирования', 'Главный корпус'),
    (3, 'Отдел кадров', 'Административный корпус'),
    (4, 'Бухгалтерия', 'Административный корпус'),
    (5, 'Отдел поддержки', 'Сервисный центр');

-- Набор из 10 сотрудников
INSERT INTO employees (employee_id, full_name, position, department_id) VALUES
    (101, 'Иванов Иван Иванович', 'Ведущий разработчик', 1),
    (102, 'Петров Пётр Петрович', 'Разработчик', 1),
    (103, 'Сидорова Анна Сергеевна', 'Инженер по тестированию', 2),
    (104, 'Кузнецов Дмитрий Олегович', 'Старший тестировщик', 2),
    (105, 'Смирнова Мария Алексеевна', 'HR-менеджер', 3),
    (106, 'Волков Алексей Николаевич', 'Рекрутер', 3),
    (107, 'Попова Елена Викторовна', 'Главный бухгалтер', 4),
    (108, 'Морозов Сергей Андреевич', 'Бухгалтер', 4),
    (109, 'Фёдорова Ольга Игоревна', 'Специалист поддержки', 5),
    (110, 'Никитин Артём Павлович', 'Старший специалист поддержки', 5);

COMMIT;
