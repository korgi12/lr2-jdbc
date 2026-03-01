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

В `pom.xml` подключен драйвер PostgreSQL:

- `org.postgresql:postgresql:42.7.4`

Подключение к БД настроено так:

- URL: `jdbc:postgresql://localhost:5432/postgres`
- Пользователь: `postgres`
- Пароль: `postgres`

## Запуск

### Требования

Перед запуском убедитесь, что установлены:

- Java 17+ (`java -version`)
- Maven 3.9+ (`mvn -version`)
- PostgreSQL (локально на `localhost:5432`)

> По умолчанию приложение использует БД `postgres`, пользователя `postgres`
> и пароль `postgres`.

### Быстрый старт

1. Соберите проект:

   ```bash
   mvn clean package
   ```

2. Запустите RMI-сервер:

   ```bash
   mvn -q exec:java -Dexec.mainClass=edu.lr2.jdbc.rmi.RmiServerApp
   ```

3. В другом терминале запустите клиент:

   ```bash
   mvn -q exec:java -Dexec.mainClass=edu.lr2.jdbc.rmi.RmiClientApp
   ```

4. В клиенте выберите пункт меню и выполните операцию (просмотр, добавление,
   удаление).

### Если PostgreSQL ещё не поднят

Можно быстро запустить БД в Docker:

```bash
docker run --name lr2-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 \
  -d postgres:16
```

После этого снова выполните шаги «Быстрый старт».

### Остановка

- Клиент: `Ctrl + C`
- Сервер: `Ctrl + C`
- PostgreSQL в Docker (если запускали так):

  ```bash
  docker stop lr2-postgres
  ```

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


## SQL-скрипт для создания таблиц и данных

Добавлен готовый скрипт `sql/init_tables_and_seed_ru.sql`, который:

- создает таблицы `departments` и `employees`;
- заполняет их русскоязычными данными;
- добавляет набор из **10 сотрудников**.

Запуск скрипта:

```bash
psql -h localhost -p 5432 -U postgres -d postgres -f sql/init_tables_and_seed_ru.sql
```

## Что такое RMI

**RMI (Remote Method Invocation)** — это механизм Java, который позволяет
вызывать методы объекта, находящегося в другом JVM/процессе (часто на другой
машине), так, будто это обычный локальный вызов.

В этом проекте это работает так:

1. Сервер поднимает удаленный объект `DepartmentEmployeeRemoteImpl`.
2. Объект регистрируется в RMI Registry под именем `DepartmentEmployeeService`.
3. Клиент получает (lookup) ссылку на этот объект.
4. Клиент вызывает методы интерфейса `DepartmentEmployeeRemote`.
5. Реальная логика выполняется на сервере и обращается к PostgreSQL через JDBC.

Итог: клиенту не нужно напрямую работать с JDBC и БД — он вызывает удаленные
методы, а сервер уже сам делает SQL-операции и возвращает результат.
