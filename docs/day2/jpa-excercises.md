```markdown
# JPA Hands-on Labs

These labs use **plain JPA (Jakarta Persistence)** with Hibernate as the implementation.  
Each lab builds on the previous one. Students can copy-paste the code into IntelliJ IDEA or any IDE.

> Prerequisites: Java 17+, Maven, and a running database (MySQL or PostgreSQL).

---

## 0. Base Project Setup

### 0.1 `pom.xml` (Starter)

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

        <!-- Logging (optional but useful) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.16</version>
        </dependency>
    </dependencies>
</project>
```

---

### 0.2 `persistence.xml` (Starter)

Create `src/main/resources/META-INF/persistence.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             version="3.0"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                                 https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">

    <persistence-unit name="labPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <!-- List managed entities explicitly or rely on classpath scanning -->
        <class>com.training.jpa.lab1.Student</class>

        <properties>
            <!-- CHANGE THESE FOR YOUR DB -->

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

            <!-- Hibernate settings (for learning only, not for prod) -->
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

---

## Lab 1 – First Entity & EM Setup

**Goal:** Create a simple JPA entity and verify that JPA can start.

### Files to provide to students

#### 1.1 `Student.java`

```java
package com.training.jpa.lab1;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // works for MySQL, Postgres (IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, length = 100)
    private String email;

    // Required no-arg constructor
    public Student() {
    }

    public Student(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and setters (generate in IDE)
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // toString for debugging
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

#### 1.2 `Lab1Main.java`

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

**Student tasks:**

- Run `Lab1Main`.
- Verify that:
    - The application starts without errors.
    - Hibernate logs table creation for `students`.

---

## Lab 2 – Basic CRUD with EntityManager

**Goal:** Implement create, read, update, delete operations.

### Files with TODOs

#### 2.1 `StudentCrudService.java`

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

    // TODO-1: Implement createStudent
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

    // TODO-2: Implement findStudentById
    public Student findStudentById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    // TODO-3: Implement updateStudentEmail
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

    // TODO-4: Implement deleteStudent
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

    // Helper: list all students
    public List<Student> findAllStudents() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s", Student.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
```

#### 2.2 `Lab2Main.java`

```java
package com.training.jpa.lab2;

import com.training.jpa.lab1.Student;

import java.util.List;

public class Lab2Main {

    public static void main(String[] args) {
        StudentCrudService service = new StudentCrudService();

        try {
            // 1. Create students
            Student s1 = service.createStudent("John", "Doe", "john.doe@example.com");
            Student s2 = service.createStudent("Jane", "Smith", "jane.smith@example.com");

            System.out.println("Created: " + s1);
            System.out.println("Created: " + s2);

            // 2. Read all
            System.out.println("=== All Students ===");
            List<Student> students = service.findAllStudents();
            students.forEach(System.out::println);

            // 3. Update
            service.updateStudentEmail(s1.getId(), "john.new@example.com");
            System.out.println("After update: " + service.findStudentById(s1.getId()));

            // 4. Delete
            service.deleteStudent(s2.getId());
            System.out.println("After delete:");
            service.findAllStudents().forEach(System.out::println);

        } finally {
            service.shutdown();
        }
    }
}
```

**Student tasks:**

- Study CRUD methods.
- Run `Lab2Main` and observe SQL logs and DB data.

---

## Lab 3 – JPQL Queries

**Goal:** Practice JPQL with conditions and counts.

### 3.1 Extend `StudentCrudService` (or create `StudentQueryService`)

Add these methods:

```java
// Find by last name
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

// Count students
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
```

### 3.2 `Lab3Main.java`

```java
package com.training.jpa.lab3;

import com.training.jpa.lab1.Student;
import com.training.jpa.lab2.StudentCrudService;

import java.util.List;

public class Lab3Main {

    public static void main(String[] args) {
        StudentCrudService service = new StudentCrudService();

        try {
            // Ensure we have some data
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

**Student tasks:**

- Run `Lab3Main`, change the last name filter, observe results.
- Optionally add another JPQL query with `LIKE`.

---

## Lab 4 – One-to-Many: Student–Course

**Goal:** Model a one-to-many association and persist it.

### 4.1 `Course.java`

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
    @JoinColumn(name = "student_id") // FK to students.id
    private Student student;

    public Course() {
    }

    public Course(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "Course{" +
               "id=" + id +
               ", title='" + title + '\'' +
               '}';
    }
}
```

> Update `persistence.xml` to add `<class>com.training.jpa.lab4.Course</class>`.

### 4.2 Add courses collection in `Student`

In `Student`:

```java
// At top
import java.util.ArrayList;
import java.util.List;

// Fields
@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Course> courses = new ArrayList<>();

public List<Course> getCourses() {
    return courses;
}

// Helper method
public void addCourse(Course course) {
    courses.add(course);
    course.setStudent(this);
}

public void removeCourse(Course course) {
    courses.remove(course);
    course.setStudent(null);
}
```

### 4.3 `Lab4Main.java`

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

            em.persist(s); // cascades to courses

            em.getTransaction().commit();

            // Fetch back
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

**Student tasks:**

- Run `Lab4Main`.
- Check DB tables `students`, `courses` and FK `student_id`.
- Try deleting the student and see cascading behavior.

---

## Lab 5 – Many-to-Many: Student–Subject

**Goal:** Model many-to-many with a join table.

### 5.1 `Subject.java`

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Student> getStudents() {
        return students;
    }
}
```

> Add `<class>com.training.jpa.lab5.Subject</class>` to `persistence.xml`.

### 5.2 Add many-to-many to `Student`

In `Student`:

```java
// imports
import com.training.jpa.lab5.Subject;
import java.util.HashSet;
import java.util.Set;

// field
@ManyToMany
@JoinTable(
    name = "student_subject",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "subject_id")
)
private Set<Subject> subjects = new HashSet<>();

public Set<Subject> getSubjects() {
    return subjects;
}

public void addSubject(Subject subject) {
    subjects.add(subject);
    subject.getStudents().add(this);
}
```

### 5.3 `Lab5Main.java`

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

**Student tasks:**

- Run `Lab5Main` and inspect `student_subject` join table.
- Experiment with adding/removing subjects.

---

## Lab 6 – Entity Lifecycle & Merge

**Goal:** Understand detached entities and `merge`.

### 6.1 `Lab6Main.java`

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

        // Step 1: Create a student
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Student s = new Student("Lifecycle", "Demo", "lifecycle@example.com");
            em.persist(s);
            em.getTransaction().commit();
            id = s.getId();
        }

        Student detachedCopy;

        // Step 2: Load and detach
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Student managed = em.find(Student.class, id);
            System.out.println("Managed: " + managed);
            em.getTransaction().commit();

            // entity is now detached (em closed)
            detachedCopy = managed;
        }

        // Step 3: Modify detached and merge
        detachedCopy.setEmail("lifecycle.updated@example.com");

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

**Student tasks:**

- Run and observe logs.
- Add comments explaining which state (managed/detached) each instance is in.

---

## Lab 7 – Transactions and Rollback

**Goal:** See rollback in action.

### 7.1 `Lab7Main.java`

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
            // Create a student
            em.getTransaction().begin();
            Student s = new Student("Tx", "Demo", "tx.demo@example.com");
            em.persist(s);
            em.getTransaction().commit();
            id = s.getId();

            // Try an update with rollback
            em.getTransaction().begin();
            Student managed = em.find(Student.class, id);
            managed.setEmail("will.not.persist@example.com");

            System.out.println("Simulating error, rolling back...");
            em.getTransaction().rollback();

            // Check in DB
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

**Student tasks:**

- Run and confirm that the email in DB remains unchanged after rollback.
- Modify code to commit instead of rollback and see the difference.

