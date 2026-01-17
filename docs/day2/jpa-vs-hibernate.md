JPA is the **standard API** (specification) for ORM in Java, while Hibernate is a **framework that implements** that specification (plus extra features).[1][2][3]

## Core idea

- **JPA (Jakarta Persistence API)**
    - A set of interfaces, annotations, and rules that define how Java objects are mapped to relational tables.
    - Lives in the `jakarta.persistence` (formerly `javax.persistence`) package.[2][1]
    - Needs a provider/implementation to actually talk to the database.

- **Hibernate**
    - A full-featured ORM framework that implements JPA and also offers many non-JPA features.[4][2]
    - Lives in the `org.hibernate` package.[1]
    - Can be used via pure Hibernate APIs or via the JPA standard APIs.

## Key differences

| Aspect          | JPA                                                                         | Hibernate                                                                    |
|-----------------|-----------------------------------------------------------------------------|------------------------------------------------------------------------------|
| Type            | **Specification** (API standard)                                            | **Implementation** (framework)                                               |
| Package         | `jakarta.persistence.*`                                                     | `org.hibernate.*`                                                            |
| What it defines | Interfaces, annotations, JPQL, basic ORM behavior                           | Concrete code, engine, dialects, caching, etc.                               |
| Portability     | Vendor-neutral: can switch between Hibernate, EclipseLink, etc. more easily | Ties you to Hibernate if you use Hibernate-specific APIs/features            |
| Features        | Only standard/common ORM features                                           | Superset: all JPA + extra (HQL, extra caching, custom types, etc.) [2][5][4] |

## How they are used together

- In most modern apps (including Spring Boot):
    - Code uses **JPA annotations and interfaces** (`@Entity`, `EntityManager`, JPQL).
    - **Hibernate runs underneath** as the JPA provider.[6][7][8][3]
- Benefit:
    - You write to the JPA API, so in theory you can swap Hibernate for another provider (EclipseLink, OpenJPA) by changing dependencies and config, not your entity/repository code.[9][3][6]

## When to say “I use JPA” vs “I use Hibernate”

- Say **“using JPA”** when:
    - Your code sticks to `jakarta.persistence` APIs and standard JPQL.
    - Hibernate is just the implementation detail.[7][6]

- Say **“using Hibernate directly”** when:
    - You use Hibernate-specific annotations/APIs (e.g. `@BatchSize`, `@Type`, `Session`, HQL-only features).[2][4]
    - Switching to another provider would require code changes.

In short: **JPA is the contract; Hibernate is one of the engines that fulfills it.**[3][4]
