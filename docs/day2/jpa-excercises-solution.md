```markdown
# JPA Hands-on Labs – Exercises with Solutions

## 0. Base Project Setup (Starter)

### 0.1 `pom.xml` (Starter)

Use this Maven configuration as a base for all labs. It includes JPA API, Hibernate, and a PostgreSQL driver (switchable to MySQL). [web:134][web:156]

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.training.jpa</groupId>
    <artifactId>jpa-labs</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <hibernate.version>6.5.2.Final</hibernate.version>
        <jakarta.persistence.version>3.1.0</jakarta.persistence.version>
    </properties>

    <dependencies>
        <!-- JPA API -->
        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>${jakarta.persistence.version}</version>
        </dependency>

        <!-- Hibernate Core (JPA implementation) -->
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>

        <!-- Choose ONE DB driver and comment the other -->

        <!-- MySQL Driver -->
        <!--
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.0.0</version>
        </dependency>
        -->

        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.3</version>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.16</version>
        </dependency>
    </dependencies>
</project>
```

### 0.2 `persistence.xml` (Starter)

Create `src/main/resources/META-INF/persistence.xml`. This config uses a local DB and Hibernate’s schema auto-generation for lab purposes. [web:134][web:169]

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             version="3.0"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                                 https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">

    <persistence-unit name="labPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <!-- List managed entities -->
        <class>com.training.jpa.lab1.Student</class>
        <class>com.training.jpa.lab4.Course</class>
        <class>com.training.jpa.lab5.Subject</class>

        <properties>
            <!-- For PostgreSQL -->
            <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/jpa_labs"/>
            <property name="jakarta.persistence.jdbc.user" value="postgres"/>
            <property name="jakarta.persistence.jdbc.password" value="postgres"/>

            <!-- For MySQL (comment Postgres and uncomment below)
            <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/jpa_labs"/>
            <property name="jakarta.persistence.jdbc.user" value="root"/>
            <property name="jakarta.persistence.jdbc.password" value="password"/>
            -->

            <!-- Hibernate (for labs only) -->
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

---

## Lab 1 – First Entity & EntityManager Setup

### Exercise

**Goal:** Create a simple JPA entity and verify that JPA can start.

Tasks:
- Create a `Student` entity with `id`, `firstName`, `lastName`, `email`.
- Add JPA annotations: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`.
- Write a `Lab1Main` class that:
    - Creates an `EntityManagerFactory`.
    - Creates and closes an `EntityManager`.
    - Prints simple log messages.

### Solution

#### `Student.java`

```java
package com.training.jpa.lab1;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL & Postgres friendly
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, length = 100)
    private String email;

    // Lab 4: one-to-many
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.training.jpa.lab4.Course> courses = new ArrayList<>();

    // Lab 5: many-to-many
    @ManyToMany
    @JoinTable(
        name = "student_subject",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<com.training.jpa.lab5.Subject> subjects = new HashSet<>();

    public Student() {
    }

    public Student(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() { return id; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public List<com.training.jpa.lab4.Course> getCourses() { return courses; }

    public Set<com.training.jpa.lab5.Subject> getSubjects() { return subjects; }

    public void addCourse(com.training.jpa.lab4.Course course) {
        courses.add(course);
        course.setStudent(this);
    }

    public void removeCourse(com.training.jpa.lab4.Course course) {
        courses.remove(course);
        course.setStudent(null);
    }

    public void addSubject(com.training.jpa.lab5.Subject subject) {
        subjects.add(subject);
        subject.getStudents().add(this);
    }

    @Override
    public String toString() {
        return "Student{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
```

#### `Lab1Main.java`

```java
package com.training.jpa.lab1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Lab1Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("labPU");
            em = emf.createEntityManager();
            System.out.println("EntityManager created successfully!");
        } finally {
            if (em != null) {
                em.close();
                System.out.println("EntityManager closed.");
            }
            if (emf != null) {
                emf.close();
                System.out.println("EntityManagerFactory closed.");
            }
        }
    }
}
```

---

## Lab 2 – Basic CRUD with EntityManager

### Exercise

**Goal:** Implement create, read, update, delete operations using `EntityManager`. [web:130][web:134]

Tasks:
- Create `StudentCrudService` with:
    - `createStudent(first, last, email)`
    - `findStudentById(id)`
    - `updateStudentEmail(id, newEmail)`
    - `deleteStudent(id)`
    - `findAllStudents()`
- Use explicit transactions and closing `EntityManager`.
- Create `Lab2Main` to:
    - Insert 2 students.
    - List all.
    - Update one email.
    - Delete one student.
    - List remaining students.

### Solution

#### `StudentCrudService.java`

```java
package com.training.jpa.lab2;

import com.training.jpa.lab1.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class StudentCrudService {

    private final EntityManagerFactory emf;

    public StudentCrudService() {
        this.emf = Persistence.createEntityManagerFactory("labPU");
    }

    public void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }

    public Student createStudent(String first, String last, String email) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Student s = new Student(first, last, email);
            em.persist(s);
            em.getTransaction().commit();
            return s;
        } finally {
            em.close();
        }
    }

    public Student findStudentById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    public void updateStudentEmail(Long id, String newEmail) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Student s = em.find(Student.class, id);
            if (s != null) {
                s.setEmail(newEmail);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void deleteStudent(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Student s = em.find(Student.class, id);
            if (s != null) {
                em.remove(s);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Student> findAllStudents() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s", Student.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    // JPQL methods used in Lab 3
    public List<Student> findByLastName(String lastName) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Student s WHERE s.lastName = :ln", Student.class)
                     .setParameter("ln", lastName)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public long countStudents() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(s) FROM Student s", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }
}
```

#### `Lab2Main.java`

```java
package com.training.jpa.lab2;

import com.training.jpa.lab1.Student;

import java.util.List;

public class Lab2Main {

    public static void main(String[] args) {
        StudentCrudService service = new StudentCrudService();

        try {
            Student s1 = service.createStudent("John", "Doe", "john.doe@example.com");
            Student s2 = service.createStudent("Jane", "Smith", "jane.smith@example.com");

            System.out.println("Created: " + s1);
            System.out.println("Created: " + s2);

            System.out.println("=== All Students ===");
            List<Student> students = service.findAllStudents();
            students.forEach(System.out::println);

            service.updateStudentEmail(s1.getId(), "john.new@example.com");
            System.out.println("After update: " + service.findStudentById(s1.getId()));

            service.deleteStudent(s2.getId());
            System.out.println("After delete:");
            service.findAllStudents().forEach(System.out::println);

        } finally {
            service.shutdown();
        }
    }
}
```

---

## Lab 3 – JPQL Queries

### Exercise

**Goal:** Use JPQL to query entities with conditions and counts. [web:136][web:139]

Tasks:
- Add to `StudentCrudService`:
    - `findByLastName(String lastName)`
    - `countStudents()`
- Write `Lab3Main` to:
    - Seed some students (if table is empty).
    - Print student count.
    - Query and print students by a last name (e.g., `"Singh"`).

### Solution

#### `Lab3Main.java`

```java
package com.training.jpa.lab3;

import com.training.jpa.lab1.Student;
import com.training.jpa.lab2.StudentCrudService;

import java.util.List;

public class Lab3Main {

    public static void main(String[] args) {
        StudentCrudService service = new StudentCrudService();

        try {
            if (service.findAllStudents().isEmpty()) {
                service.createStudent("Alice", "Singh", "alice.singh@example.com");
                service.createStudent("Bob", "Singh", "bob.singh@example.com");
                service.createStudent("Charlie", "Brown", "charlie.brown@example.com");
            }

            System.out.println("Total students: " + service.countStudents());

            System.out.println("=== Students with lastName = 'Singh' ===");
            List<Student> singhList = service.findByLastName("Singh");
            singhList.forEach(System.out::println);

        } finally {
            service.shutdown();
        }
    }
}
```

---

## Lab 4 – One‑to‑Many: Student–Course

### Exercise

**Goal:** Model and persist a one‑to‑many association. [web:167][web:170]

Tasks:
- Create `Course` entity with fields `id`, `title`, `student`.
- Map:
    - `Student` → `List<Course>` (`@OneToMany(mappedBy = "student", cascade = ALL)`).
    - `Course` → `Student` (`@ManyToOne` with `@JoinColumn`).
- Implement helper `addCourse(Course c)` in `Student`.
- In `Lab4Main`:
    - Create one student with 2–3 courses.
    - Persist.
    - Clear persistence context and fetch back the student, printing their courses.

### Solution

#### `Course.java`

```java
package com.training.jpa.lab4;

import com.training.jpa.lab1.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public Course() {
    }

    public Course(String title) {
        this.title = title;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public Student getStudent() { return student; }

    public void setStudent(Student student) { this.student = student; }

    @Override
    public String toString() {
        return "Course{" +
               "id=" + id +
               ", title='" + title + '\'' +
               '}';
    }
}
```

#### `Lab4Main.java`

```java
package com.training.jpa.lab4;

import com.training.jpa.lab1.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Lab4Main {

    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("labPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Student s = new Student("Ravi", "Kumar", "ravi.kumar@example.com");
            Course c1 = new Course("JPA Fundamentals");
            Course c2 = new Course("Spring Data JPA");

            s.addCourse(c1);
            s.addCourse(c2);

            em.persist(s);

            em.getTransaction().commit();

            em.clear();

            Student found = em.find(Student.class, s.getId());
            System.out.println("Student: " + found);
            System.out.println("Courses:");
            found.getCourses().forEach(System.out::println);

        } finally {
            em.close();
            emf.close();
        }
    }
}
```

---

## Lab 5 – Many‑to‑Many: Student–Subject

### Exercise

**Goal:** Model a many‑to‑many mapping with a join table. [web:161][web:164]

Tasks:
- Create `Subject` entity with `id`, `name`, `Set<Student> students`.
- Add to `Student`:
    - `Set<Subject> subjects` with `@ManyToMany` + `@JoinTable`.
- Implement helper `addSubject(Subject s)` in `Student`.
- In `Lab5Main`:
    - Create 2 students and 3 subjects.
    - Enrol students into multiple subjects.
    - Persist and then fetch back students with their subjects.

### Solution

#### `Subject.java`

```java
package com.training.jpa.lab5;

import com.training.jpa.lab1.Student;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "subjects")
    private Set<Student> students = new HashSet<>();

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public Set<Student> getStudents() { return students; }

    @Override
    public String toString() {
        return "Subject{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
```

#### `Lab5Main.java`

```java
package com.training.jpa.lab5;

import com.training.jpa.lab1.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Lab5Main {

    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("labPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Student s1 = new Student("Anita", "Sharma", "anita.sharma@example.com");
            Student s2 = new Student("Vijay", "Patel", "vijay.patel@example.com");

            Subject sub1 = new Subject("Java");
            Subject sub2 = new Subject("Databases");
            Subject sub3 = new Subject("Spring");

            s1.addSubject(sub1);
            s1.addSubject(sub2);

            s2.addSubject(sub2);
            s2.addSubject(sub3);

            em.persist(s1);
            em.persist(s2);

            em.getTransaction().commit();

            em.clear();

            System.out.println("=== Students and their Subjects ===");
            Student found1 = em.find(Student.class, s1.getId());
            System.out.println(found1 + " -> " + found1.getSubjects());

            Student found2 = em.find(Student.class, s2.getId());
            System.out.println(found2 + " -> " + found2.getSubjects());

        } finally {
            em.close();
            emf.close();
        }
    }
}
```

---

## Lab 6 – Entity Lifecycle & `merge`

### Exercise

**Goal:** Understand managed vs detached entities and `merge`. [web:134][web:140]

Tasks:
- Create a student and commit.
- Close the `EntityManager` (entity becomes detached).
- Modify the detached instance.
- Use a new `EntityManager` to `merge` the changes and commit.
- Verify updated data from DB.

### Solution

#### `Lab6Main.java`

```java
package com.training.jpa.lab6;

import com.training.jpa.lab1.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Lab6Main {

    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("labPU");

        Long id;

        // Create
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Student s = new Student("Lifecycle", "Demo", "lifecycle@example.com");
            em.persist(s);
            em.getTransaction().commit();
            id = s.getId();
        }

        Student detachedCopy;

        // Load & detach
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Student managed = em.find(Student.class, id);
            System.out.println("Managed: " + managed);
            em.getTransaction().commit();
            detachedCopy = managed; // will be detached after EM closes
        }

        // Modify detached
        detachedCopy.setEmail("lifecycle.updated@example.com");

        // Merge
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Student merged = em.merge(detachedCopy);
            System.out.println("Merged entity: " + merged);
            em.getTransaction().commit();
        }

        // Verify
        try (EntityManager em = emf.createEntityManager()) {
            Student s = em.find(Student.class, id);
            System.out.println("After merge from DB: " + s);
        }

        emf.close();
    }
}
```

---

## Lab 7 – Transactions and Rollback

### Exercise

**Goal:** See rollback in action. [web:130][web:140]

Tasks:
- Create a student and commit.
- Start a new transaction, update the email, then deliberately roll back.
- Verify that the DB still has the old email.

### Solution

#### `Lab7Main.java`

```java
package com.training.jpa.lab7;

import com.training.jpa.lab1.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Lab7Main {

    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("labPU");
        EntityManager em = emf.createEntityManager();

        Long id;

        try {
            em.getTransaction().begin();
            Student s = new Student("Tx", "Demo", "tx.demo@example.com");
            em.persist(s);
            em.getTransaction().commit();
            id = s.getId();

            em.getTransaction().begin();
            Student managed = em.find(Student.class, id);
            managed.setEmail("will.not.persist@example.com");

            System.out.println("Simulating error, rolling back...");
            em.getTransaction().rollback();

            em.clear();
            Student fromDb = em.find(Student.class, id);
            System.out.println("Email after rollback (from DB): " + fromDb.getEmail());

        } finally {
            em.close();
            emf.close();
        }
    }
}
```