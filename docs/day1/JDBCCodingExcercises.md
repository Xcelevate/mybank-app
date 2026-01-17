Here is a self-contained Markdown file you can save as `jdbc-exercises.md` and share with students. They can copy-paste code directly.

```markdown
# JDBC Hands-on Exercises


## Practise the IDE Features

1. Try IntelliJ feature using "IDE Features Trainer" plugin - https://plugins.jetbrains.com/plugin/8554-ide-features-trainer
2. Read IntelliJ Shortcuts: https://www.jetbrains.com/help/idea/mastering-keyboard-shortcuts.html
3. Watch: https://www.youtube.com/watch?v=eimdQeNG-68

## Exercise 1: Environment Setup & Basic Connection

**Goal:** Configure the JDBC driver, connect to the database, and print basic metadata.

### Steps

1. Create a database:
2. Create a user
3. Add JDBC driver to your project (e.g., Maven dependency for MySQL):

   ```xml
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <version>8.4.0</version>
   </dependency>
   ```

4. Create `TestConnection.java`and print database product name, version and driver.


### Tasks

- Run with correct credentials (should print DB details).
- Change the password to a wrong value and observe `SQLException`.
- Restore correct values and verify success again.

---

## Exercise 2: Create Table & CRUD Using `Statement`

**Goal:** Use plain `Statement` for DDL and simple CRUD; understand limitations and SQL injection risk.

### Database Setup

Run in `training_db`:

```sql
CREATE TABLE IF NOT EXISTS employees (
    emp_id   INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    salary   DOUBLE NOT NULL
);
```

### Code: `EmployeeStatementDemo.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeStatementDemo {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/training_db";
        String user = "training_user";
        String password = "training_pwd";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement()) {

            // Ensure table exists (idempotent)
            String createSql =
                    "CREATE TABLE IF NOT EXISTS employees (" +
                    "emp_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "salary DOUBLE NOT NULL" +
                    ")";
            stmt.executeUpdate(createSql);

            // INSERT
            String insertSql1 =
                    "INSERT INTO employees (name, salary) VALUES ('Alice', 60000)";
            String insertSql2 =
                    "INSERT INTO employees (name, salary) VALUES ('Bob', 55000)";
            stmt.executeUpdate(insertSql1);
            stmt.executeUpdate(insertSql2);

            // SELECT
            String querySql = "SELECT emp_id, name, salary FROM employees";
            try (ResultSet rs = stmt.executeQuery(querySql)) {
                System.out.println("Employees:");
                while (rs.next()) {
                    int id = rs.getInt("emp_id");
                    String name = rs.getString("name");
                    double salary = rs.getDouble("salary");
                    System.out.printf("%d, %s, %.2f%n", id, name, salary);
                }
            }

            // UPDATE
            String updateSql =
                    "UPDATE employees SET salary = 65000 WHERE name = 'Alice'";
            int updated = stmt.executeUpdate(updateSql);
            System.out.println(updated + " row(s) updated.");

            // DELETE
            String deleteSql =
                    "DELETE FROM employees WHERE name = 'Bob'";
            int deleted = stmt.executeUpdate(deleteSql);
            System.out.println(deleted + " row(s) deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Tasks

- Add another `INSERT` using string concatenation with a “user input” variable.
- Explore why this is unsafe (SQL injection).

---

## Exercise 3: Secure CRUD with `PreparedStatement` (DAO)

**Goal:** Use `PreparedStatement` with parameters for secure, reusable CRUD operations.

### Code: `EmployeeDao.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDao {

    private final String url = "jdbc:mysql://localhost:3306/training_db";
    private final String user = "training_user";
    private final String password = "training_pwd";

    public void addEmployee(String name, double salary) throws SQLException {
        // Using prepared statement insert a new employee
        String sql = "INSERT INTO employees (name, salary) VALUES (?, ?)";
        
    }

    public void updateSalary(int empId, double newSalary) throws SQLException {
        // Using prepared statement insert a new employee
        String sql = "UPDATE employees SET salary = ? WHERE emp_id = ?";
        
        
    }

    public void deleteEmployee(int empId) throws SQLException {
        String sql = "DELETE FROM employees WHERE emp_id = ?";
        // Using prepared statement delete an employee
    }

    public void listEmployees() throws SQLException {
        
        // Using prepared statement list all employee
        String sql = "SELECT emp_id, name, salary FROM employees";

    }
}
```

### Code: `EmployeePreparedDemo.java`

```java
import java.sql.SQLException;

public class EmployeePreparedDemo {

    public static void main(String[] args) {
        EmployeeDao dao = new EmployeeDao();

        try {
            dao.addEmployee("Charlie", 50000);
            dao.addEmployee("Diana", 72000);

            dao.listEmployees();

            dao.updateSalary(1, 70000);
            dao.deleteEmployee(2);

            dao.listEmployees();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Addintional Tasks

- Add a `findById(int empId)` method in EmployeeDao that will help to find an employee given an employee id. Call this method from EmployeePreparedDemo. 
- Ensure no SQL is built with string concatenation.

---

## Exercise 4: `ResultSet` & `ResultSetMetaData`

**Goal:** Iterate a `ResultSet` and use metadata to print column names and values generically.

### Code: `ResultSetMetadataDemo.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetMetadataDemo {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/training_db";
        String user = "training_user";
        String password = "training_pwd";

        String sql = "SELECT emp_id, name, salary FROM employees ORDER BY salary DESC";
        //Using result metadata print all the column names and values
        
    }
}
```

### Additional Tasks

- Modify SQL to filter employees with salary > 60000.
- Print column type names from `ResultSetMetaData`.

---

## Exercise 5: Transactions – Fund Transfer

**Goal:** Implement a transfer between two accounts using manual transaction control.

### Database Setup

```sql
CREATE TABLE IF NOT EXISTS accounts (
    acc_id   INT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    balance  DOUBLE NOT NULL
);

INSERT INTO accounts (acc_id, name, balance) VALUES (1, 'Account A', 10000)
    ON DUPLICATE KEY UPDATE name = 'Account A', balance = 10000;

INSERT INTO accounts (acc_id, name, balance) VALUES (2, 'Account B', 5000)
    ON DUPLICATE KEY UPDATE name = 'Account B', balance = 5000;
```

### Code: `AccountService.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {

    private final String url = "jdbc:mysql://localhost:3306/training_db";
    private final String user = "training_user";
    private final String password = "training_pwd";

    public void transfer(int fromAccId, int toAccId, double amount) throws SQLException {
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE acc_id = ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE acc_id = ?";
        
        // Using prepared statement withdraw from "fromAccId" and deposit it to "toAccId" under a transaction.
        // Handle exceptions and close statements carefully
    }

    public void printBalances() throws SQLException {
        String sql = "SELECT acc_id, name, balance FROM accounts";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Account balances:");
            while (rs.next()) {
                System.out.printf("%d - %s: %.2f%n",
                        rs.getInt("acc_id"),
                        rs.getString("name"),
                        rs.getDouble("balance"));
            }
        }
    }
}
```

### Code: `AccountDemo.java`

```java
import java.sql.SQLException;

public class AccountDemo {

    public static void main(String[] args) {
        AccountService service = new AccountService();

        try {
            service.printBalances();
            service.transfer(1, 2, 2000);
            service.printBalances();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Tasks
-
- Force an error (e.g., use a non-existent toAccId `acc_id`) and verify that balances remain unchanged.
- Explore why rollback is critical.

---

## Exercise 6: Batch Inserts with `PreparedStatement`

**Goal:** Use `addBatch()` and `executeBatch()` for bulk inserts.

### Code: `EmployeeBatchInsert.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeBatchInsert {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/training_db";
        String user = "training_user";
        String password = "training_pwd";
        
        //print start time of batch insert here...
        batchInsertEmployees(...);
        //print end time of batch insert here...
        //print start time of invdividual insert here...
        individualInsertEmployees(...);
        //print end time of invdividual insert here...
        
    }
    
    public void batchInsertEmployees(String url, String user, String password) {
        String sql = "INSERT INTO employees (name, salary) VALUES (?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            // Create 100 employee in the employee table with names as 1, 2, 3, 4... and Salary as 1001, 1002, 1003, ...etc

            con.commit();
            System.out.println("All batches committed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void individualInsertEmployees(String url, String user, String password) {
            // Create 1000 employee in the employee table with names as Emp1, Emp2, Emp3, Emp4... and Salary as 1001, 1002, 1003, ...etc

        
    }
    
    
    
}
```

### Additional Tasks

- Compare execution time between:
    - 100 individual inserts (no batch, autocommit true).
    - 100 inserts with batch + single commit.

---

## Exercise 7: Stored Procedure & `CallableStatement`

**Goal:** Call a stored procedure with IN and OUT parameters.

### Database Setup (MySQL example)

```sql
DELIMITER $$

CREATE PROCEDURE get_employee_salary(
    IN p_emp_id INT,
    OUT p_salary DOUBLE
)
BEGIN
    SELECT salary INTO p_salary
    FROM employees
    WHERE emp_id = p_emp_id;
END $$

DELIMITER ;
```

### Code: `CallableDemo.java`

```java
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

public class CallableDemo {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/training_db";
        String user = "training_user";
        String password = "training_pwd";

        int empId = 1;
        String sql = "{ call get_employee_salary(?, ?) }";

        //Call the stored procedure here and print his salary
    }
}
```

### Tasks

- Modify the procedure to return an error or default if employee does not exist.
- Add another procedure that returns all employees with salary above a threshold using a `ResultSet`.

---

## Exercise 8: Try-with-resources Refactor

**Goal:** Refactor manual closing code to use try-with-resources.

### Example Before

```java
Connection con = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

try {
    con = DriverManager.getConnection(url, user, password);
    pstmt = con.prepareStatement(sql);
    rs = pstmt.executeQuery();
    while (rs.next()) {
        System.out.println(rs.getString("name"));
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    if (rs != null) rs.close();
    if (pstmt != null) pstmt.close();
    if (con != null) con.close();
}
```

### Example After

```java
try (Connection con = DriverManager.getConnection(url, user, password);
     PreparedStatement pstmt = con.prepareStatement(sql);
     ResultSet rs = pstmt.executeQuery()) {

    while (rs.next()) {
        System.out.println(rs.getString("name"));
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### Tasks

- Take one of your previous exercises and refactor to try-with-resources.
- Explore why you still need a `finally` (e.g., for custom non-JDBC resources) in certain situations.

---

## Exercise 9 (Advanced): Isolation Level Observation

**Goal:** Observe behavior differences of isolation levels (`READ_COMMITTED`, `REPEATABLE_READ`, etc.).

> This works best if you can run two programs in parallel (Reader and Writer).

### Reader Program: `IsolationReader.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IsolationReader {

    public static void main(String[] args) throws InterruptedException {
        String url = "jdbc:mysql://localhost:3306/training_db";
        String user = "training_user";
        String password = "training_pwd";

        String sql = "SELECT balance FROM accounts WHERE acc_id = 1";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Try also: TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE

            ResultSet rs1 = pstmt.executeQuery();
            if (rs1.next()) {
                System.out.println("First read balance = " + rs1.getDouble(1));
            }

            System.out.println("Sleeping 20 seconds. Run Writer now...");
            Thread.sleep(20_000);

            ResultSet rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                System.out.println("Second read balance = " + rs2.getDouble(1));
            }

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Writer Program: `IsolationWriter.java`

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IsolationWriter {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/training_db";
        String user = "training_user";
        String password = "training_pwd";

        String sql = "UPDATE accounts SET balance = balance + 1000 WHERE acc_id = 1";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            int rows = pstmt.executeUpdate();
            System.out.println("Writer updated rows: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Tasks

- Run `IsolationReader` (it will sleep).
- During sleep, run `IsolationWriter`.
- Observe:
    - At `READ_COMMITTED`, does the second read see the new balance?
    - At `REPEATABLE_READ`, does it still see the old balance?

---
