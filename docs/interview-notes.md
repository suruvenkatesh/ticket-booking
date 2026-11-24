# Interview notes

## Week 1: equals, hashCode, HashSet, records

### What is casting, and why check `instanceof` first?
Casting tells Java how to view an object: `(Seat) o` means "treat `o` as a Seat". It doesn't change the object itself, only the type of the variable pointing to it. If the object isn't really a `Seat`, the cast throws a `ClassCastException`, so we first check `o instanceof Seat`, which returns `true` or `false`. Bonus: `null instanceof Seat` is `false`, so `equals(null)` safely returns `false`.

### Why must you override `hashCode()` when you override `equals()`?
Because `HashSet` and `HashMap` call `hashCode()` first to choose which drawer (bucket) to look in, and only then call `equals()` on what's inside. If two equal objects have different hashCodes, they land in different drawers, `equals()` is never called, and the set stores both as duplicates. The rule (the equals/hashCode contract): **equal objects must have equal hashCodes.**

### How does a `HashSet` decide whether an element is a duplicate?
1. It calls `hashCode()` and turns the number into a drawer index.
2. In that drawer, it only looks at elements with exactly the same hashCode.
3. It calls `equals()` on those. If one returns `true`, it's a duplicate and `add()` returns `false`.

`hashCode()` only narrows the search; `equals()` makes the final decision.

### What's the difference between identity and value equality?
Identity means the same object in memory, checked with `a == b`. Value equality means two separate objects hold the same data, checked with an overridden `a.equals(b)`. The default `equals()` and `hashCode()` from `Object` only check identity. Example: `add(s); add(s);` gives size 1 even without overriding, because there is one object, but adding two `new Seat("A", 1)` gives size 2 unless `Seat` has value equality.

### Is the default `hashCode()` unique? If two hashCodes are equal, are the objects equal?
No to both.
- **Not unique.** The default `hashCode()` is a generated number (not the memory address in modern JVMs). Two objects collide only about 1 in 2 billion times, but among 200,000 objects there were 9 repeats (the birthday problem).
- **Equal hashCodes don't mean equal objects.** `new Seat("A", 32)` and `new Seat("B", 1)` both have hashCode 2047 on Java 21 but aren't equal. So do `"Aa"` and `"BB"` (both 2112).

The rule only goes one way: equal objects → equal hashCodes.

### Why is `HashSet.contains()` faster than `List.contains()`?
`HashSet.contains()` uses `hashCode()` to jump straight to one drawer and checks only the few elements in it: **O(1)** on average. `List.contains()` calls `equals()` on every element one by one: **O(n)**. With 10,000 seats, that's a handful of checks versus up to 10,000.

### Is `return 42;` a valid `hashCode()`? Is it a good one?
Valid: equal objects still get equal hashCodes, so the contract holds and the set still gives correct results. Bad: every object lands in the same drawer, so each lookup calls `equals()` on everything and the set becomes as slow as a list (O(n)). A good `hashCode()` spreads objects across many drawers, for example `Objects.hash(row, number)`.

### What does a record generate for you?
For `record Seat(String row, int number)`, Java generates `private final` fields, a constructor, accessors `row()` and `number()` (no `get` prefix), and `equals()`, `hashCode()` and `toString()` based on all the fields. So a record has value equality out of the box: two seats with the same row and number are equal.

## Week 1: Venue

### Why did we use a `Set` for the venue's seats, and not a `List`?
Two reasons. **No duplicates:** a hall can't have seat A1 twice, and a `Set` doesn't allow duplicates (a `List` does). **Fast to check:** `contains()` jumps straight to one drawer using `hashCode()`, so "does this venue have seat A1?" is O(1) on average, while a `List` checks every seat (O(n)).

### What does `Set.copyOf` do?
It makes a locked copy of the set. Two effects:
- **Own copy:** changing the original set later doesn't change the venue.
- **Locked:** nobody can add or remove seats through `venue.seats()`. Trying it throws `UnsupportedOperationException`.

This makes `Venue` fully immutable: without it, the record would be only shallowly immutable, because the field couldn't change but the set inside could. To "remodel" a venue, you create a new `Venue` instead of changing the old one.

### Why does `hasSeat` return `false` instead of throwing an error?
Because "do you have this seat?" is a normal yes/no question, and "no" is a normal answer, not a mistake. Exceptions are for wrong input, like a blank venue name. A method named `has...` or `is...` should just return `true` or `false`.

## Stage 2: Spring Boot basics (steps 1–10)

Read each answer out loud. Aim for 30–60 seconds, and finish with the example from our project.

### What does `spring-boot-starter-parent` do?
It's a parent POM, so our `pom.xml` inherits its settings. Mainly it manages versions: it pins versions of Spring, JUnit, Jackson and many other libraries that are tested to work together, so we leave `<version>` off our dependencies. It also sets defaults such as the Java version, UTF-8 encoding and the Maven plugin setup. To upgrade Spring Boot, we change one number. If a company project already has its own parent, you get the same version management by importing `spring-boot-dependencies` as a BOM.

### What is a Spring Boot starter?
A starter is one dependency that brings a whole set of libraries for a job. `spring-boot-starter-webmvc` gives us Spring MVC, an embedded Tomcat server and Jackson for JSON. `spring-boot-starter-validation` gives us Hibernate Validator. Without starters, we'd have to find and list each library and pick versions that work together.

### What does `@SpringBootApplication` do?
It combines three annotations:
- **`@SpringBootConfiguration`:** this class can define beans.
- **`@EnableAutoConfiguration`:** Spring Boot looks at what's on the classpath and sets things up for us. It found Tomcat, so it started a web server on port 8080. If we define our own bean of the same kind, ours wins.
- **`@ComponentScan`:** Spring looks for `@Component`, `@Service`, `@Repository` and `@RestController` classes in the main class's package and every package under it. That's how it found our `EventController` in `com.ticketbooking.web`.

### What's the difference between `@Controller` and `@RestController`?
`@RestController` is `@Controller` plus `@ResponseBody`. Whatever its methods return is written straight into the response as data, usually JSON. A plain `@Controller` returns the name of a view, like an HTML page template. For REST APIs, we use `@RestController`.

### How does Spring turn a Java object into JSON, and back?
It uses an HTTP message converter backed by Jackson, a separate JSON library that Spring Boot includes by default. When a controller method returns our `EventResponse` record, Jackson writes it as `{"id": ..., "name": ..., "venueName": ...}`, using the record's field names as keys. It works the other way for `@RequestBody`: the incoming JSON becomes a `CreateEventRequest`. The request needs `Content-Type: application/json`, or Spring replies 415. In Spring Boot 4 the converter is `JacksonJsonHttpMessageConverter` (Jackson 3). Before that it was `MappingJackson2HttpMessageConverter` (Jackson 2).

### What is a DTO, and why not return your domain object directly?
A DTO (data transfer object) is a simple class, often a record, shaped exactly like the JSON an API sends or receives. We return `EventResponse`, not `Event`. There are two reasons. First, we choose what goes out: `Event` holds the status of every seat, which clients don't need. Second, the domain class can change without breaking the API's JSON. With a database, this matters even more, because entities can hold things like password hashes, or lazy-loaded data that fails when Jackson tries to read it.

### `HashMap` vs `ConcurrentHashMap`?
`HashMap` isn't thread-safe. If two threads write to it at the same time, one write can be lost, or the map's inside can break. `ConcurrentHashMap` is built for many threads at once. Reads usually don't lock, and writes lock only a small part of the map, not the whole thing, so it's much faster than `Hashtable` or `Collections.synchronizedMap`. It doesn't allow `null` keys or values. One catch: it makes each single call safe, but not a "check, then act" sequence of calls. For that, use its atomic methods, like `putIfAbsent` or `compute`. Our `EventRepository` uses one, because Tomcat runs many requests at once, each on its own thread.

### What are inversion of control and dependency injection?
Inversion of control means a class doesn't create the things it needs. Something else does, and in Spring that's the container. Dependency injection is how it's done: the class lists what it needs, and Spring passes those objects in. Our `EventController` asks for an `EventService` in its constructor and never writes `new`. This matters because, with `new`, two classes would each get their own `EventRepository`, with separate storage. With DI, they share one.

### Constructor injection vs field injection?
Constructor injection is preferred:
- The field can be `final`, so it can't be swapped later.
- Everything a class needs is visible in one place, its constructor.
- If a dependency is missing, the app fails at startup instead of later.
- Unit tests can pass in objects directly, like `new EventController(new EventService(...))`, without Spring.

Field injection (`@Autowired` on a field) hides the dependencies, can't use `final`, and needs Spring or reflection in tests. When a class has only one constructor, Spring uses it automatically, so `@Autowired` isn't needed.

### What is a bean, and what's the default scope?
A bean is an object that Spring creates and manages. Our `EventRepository`, `EventService` and `EventController` are all beans. The default scope is **singleton**: one instance for the whole application, shared by everyone who asks. Other scopes include `prototype` (a new instance each time it's requested) and the web scopes `request` and `session`. Because a singleton is shared by all requests at once, it must be stateless or thread-safe. That's another reason our repository uses `ConcurrentHashMap`. Spring creates beans in dependency order, working it out from the constructors: `EventRepository` first, then `EventService`, then `EventController`.

### `@Component` vs `@Service` vs `@Repository`?
All three make a class a bean that component scanning finds. `@Service` and `@Repository` are specialised versions of `@Component`: their names show the class's job, business logic or storage. There's one real extra: `@Repository` turns database-specific exceptions into Spring's `DataAccessException` family. `@RestController` is a specialised `@Controller`, which is itself a `@Component`.

### Why have a service layer?
Layered architecture gives each layer one job, and each layer only talks to the one below it:
- **Controller:** web concerns. Reading JSON, `@Valid`, and picking status codes.
- **Service:** business rules, like "new events go into City Hall". Later, transactions too.
- **Repository:** storage.

So each layer can change on its own. When we add PostgreSQL, only the repository changes. Services can also be reused by several controllers, and tested without the web layer.

### Which HTTP status codes did we see, and what do they mean?
- **200 OK:** success. A plain return value from a controller gets this.
- **201 Created:** a POST created something. We send it with `ResponseEntity.status(HttpStatus.CREATED)`.
- **400 Bad Request:** the input was invalid, for example a blank event name.
- **404 Not Found:** nothing exists at that URL.
- **405 Method Not Allowed:** the URL exists, but not for that method, like `POST` where only `GET` is mapped.
- **415 Unsupported Media Type:** the body isn't in a format the endpoint accepts, for example a missing `Content-Type: application/json`.

Any status code means the server is running. If it isn't, you get "connection refused" instead.

### `@NotNull` vs `@NotEmpty` vs `@NotBlank`? And what happens when `@Valid` fails?
- **`@NotNull`:** rejects only `null`.
- **`@NotEmpty`:** also rejects `""` and empty lists.
- **`@NotBlank`:** also rejects text that's only spaces, like `"  "`. It's for `String`s only.

The rules sit on the DTO fields. `@Valid` on the `@RequestBody` parameter tells Spring to check them before calling the method. If a rule fails, Spring throws `MethodArgumentNotValidException` and replies 400, and our method never runs. These annotations come from the Jakarta Validation standard, and Hibernate Validator is the library that checks them.

## Stage 3: Database setup (farmer-onboarding steps 2–5)

Read each answer out loud. Aim for 30–60 seconds, and finish with the example from our project.

### What are Docker images, containers and volumes? Why use Docker Compose locally?
An image is a packaged piece of software with everything it needs, like `postgres:17-alpine`. A container is a running copy of an image: you can stop, delete and recreate it in seconds. Containers are throwaway, so data goes in a volume, storage that Docker keeps outside the container. It survives `docker compose down`. Compose lists all the containers a project needs in one file, `compose.yaml`, so a new developer runs `docker compose up -d` and gets exactly the same PostgreSQL version as everyone else. In our project, PostgreSQL 17 runs this way, with the named volume `postgres-data`.

### Why did we publish the database port as `127.0.0.1:5432:5432`?
The format is address, then the port on our machine, then the port inside the container. `127.0.0.1` means only this machine can connect. Without it, Docker listens on every network interface, so anyone on the network, or the internet on a server, could reach the database. On Linux servers, ports that Docker publishes also skip the ufw firewall. So the safe pattern is to bind to `127.0.0.1` and put nginx in front.

### What is JDBC, and what is a JDBC driver?
JDBC is Java's standard API for relational databases: open a connection, run SQL, read the results. The same Java code works with any database. A driver implements JDBC for one database: it turns JDBC calls into that database's network protocol. We use the PostgreSQL driver with Maven scope `runtime`, because our code only uses the JDBC interfaces, and the driver is needed only when the app runs. Hibernate, Flyway and HikariCP all use JDBC underneath.

### What is a connection pool, and why do we need one?
Opening a database connection is slow: a network handshake, a login and some setup. A pool opens a few connections in advance, lends one to each request, and takes it back afterwards. Spring Boot uses HikariCP by default, with at most 10 connections. If all 10 are busy, the next request waits, and after 30 seconds by default it fails. So the pool size limits how many requests can use the database at the same time. In our log, `HikariPool-1 - Start completed` showed the pool was ready.

### JPA vs Hibernate vs Spring Data JPA?
JPA is the specification: a standard set of interfaces and annotations, like `@Entity` and `EntityManager`, for ORM, which maps Java objects to tables. Hibernate is the most popular implementation of JPA: it does the real work and writes the SQL. Spring Data JPA sits on top: you write a repository interface, and Spring generates the code for `save`, `findById` and more, using JPA underneath. In our project, one starter, `spring-boot-starter-data-jpa`, brings all three plus HikariCP.

### What is a Hibernate dialect? What happens if the database is down when the app starts?
SQL differs a little between databases: data types, paging, functions. The dialect tells Hibernate which flavour to write. Hibernate picks it at startup by asking the database for its name and version. Our log showed `PostgreSQLDialect` and version 17.12. If the database is down, Hibernate can't ask, the `entityManagerFactory` bean fails, and the whole app refuses to start. That's fail fast, which is better than starting and failing on a real user's request. Don't hide it by hard-coding the dialect.

### How do you read a long stack trace?
Each layer catches the error below it, wraps it and adds its own message, so you get a chain of `Caused by:` sections. Start at the last `Caused by:`, which is the root cause, and read upward to see how it spread. Then find the first line from your own package. In our test with PostgreSQL stopped, the root cause was `java.net.ConnectException: Connection refused`: nothing was listening on port 5432. On top of it came the driver's `PSQLException`, then Hibernate's dialect error, and finally Spring's `BeanCreationException`.

### What is `spring.jpa.open-in-view`, and why do many teams turn it off?
Spring Boot turns it on by default and logs a warning about it. It keeps the Hibernate session, and its database connection, open for the whole web request, even while the JSON is being written. That makes lazy loading work anywhere, but it holds connections longer, which can use up the pool under load. It also hides extra queries that run while the response is built. Many teams set it to `false` and load what they need in the service layer. We'll turn it off with our first entity.

### Why use database migrations, like Flyway?
The database structure has to change along with the code, in the same way, on every laptop and on the test and production servers. Flyway does this with numbered SQL files kept in Git, like `V1__create_farmer_application.sql`. At startup, it runs only the files that haven't run on that database yet, in order, and records each one. So database changes get code review and history like any other code, and every environment ends up identical. Spring Boot runs Flyway automatically at startup, after the pool starts and before Hibernate.

### How does Flyway know what to run? What if you edit a migration that already ran?
It keeps its own table, `flyway_schema_history`, with one row per script that ran: the version, the description, the script name, a checksum and a success flag. At startup it compares the files with that table and runs only the new ones. The checksum is a fingerprint of the file's contents. If someone edits a script that already ran, the checksum no longer matches, validation fails, and the app refuses to start. The rule: never edit a migration that ran; add a new one, like `V2__add_bank_details.sql`. On PostgreSQL, each migration also runs in a transaction, so a failed script leaves no half-done changes.

### Hibernate can create tables with `ddl-auto`. Why use Flyway instead?
`ddl-auto=update` compares the entity classes with the database and adds missing tables and columns. It never renames or drops a column. If you rename `mobileNumber` to `mobile`, it adds a new, empty `mobile` column, and the app stops seeing the old data. It can't move or fix data either, like filling a new required column for rows that already exist. It also runs silently at startup, so there's no file in Git to review, and environments drift apart. `create` and `create-drop` are worse: they delete every table at startup. Spring Boot's `schema.sql` runs the same script on every startup, so it can't change a table that already exists. In production, Flyway or Liquibase owns the schema, and Hibernate is set to `validate` (or `none`): it only checks that the entities match the tables. `update` and `create` are fine for demos and throwaway tests.

### Flyway vs Liquibase?
Both manage database migrations, and both are common in companies. Flyway is simpler: plain SQL files with the version number in the file name. Liquibase uses changelogs written in XML, YAML, JSON or SQL, can describe changes in a database-neutral way, and has rollback built in. Flyway suits a team on one database that wants plain SQL. Liquibase suits teams that support several databases or want generated rollbacks. We chose Flyway.

### UUID or an auto-increment number as the primary key?
An auto-increment number (1, 2, 3) is small and fast, and new rows go at the end of the index. But the ids are guessable in URLs, and the id exists only after the database saves the row. A UUID can be created in Java before saving, can't be guessed, and stays unique across services and databases, which suits microservices. The costs: 16 bytes instead of 8, and random UUIDs scatter new rows across the index, which can slow down very big tables. Time-ordered UUIDs (version 7) reduce that. Our `farmer_application.id` is a `UUID`.

### `TIMESTAMP` vs `TIMESTAMP WITH TIME ZONE`?
`TIMESTAMP WITH TIME ZONE` (`timestamptz`) stores an exact moment. PostgreSQL keeps it in UTC and shows it in the session's time zone. Plain `TIMESTAMP` is only a date and a clock time with no zone, so "09:00" is unclear when servers and users are in different time zones. Our `created_at` is `timestamptz`. In psql, a row created at 14:39 India time showed as `09:09...+00`: the same moment, written in UTC. In Java it maps to `Instant` or `OffsetDateTime`.

### What does a primary key give you? What is an index?
A primary key identifies each row: it must be unique and can't be null. PostgreSQL builds an index for it automatically. We saw `"farmer_application_pkey" PRIMARY KEY, btree (id)`. An index is a sorted structure, by default a B-tree, like a phone book: the database finds one id among millions in a few steps, instead of reading every row. Indexes make reads faster but writes slightly slower, and they use disk, so you add them to columns you search by often.

### Why `NOT NULL` in the database, when Java already validates with `@NotBlank`?
Defence in depth. Java validation gives the user a clear 400 error. But data can also come from scripts, other code, or a bug that skips validation. The database is the last line of defence: it refuses bad rows wherever they come from. In psql, inserting a row without `farmer_name` failed with "violates not-null constraint". From Java, Spring turns that into a `DataIntegrityViolationException`.

### Basic SQL: how do the commands map to CRUD and REST?
Create is `INSERT` (REST `POST`), read is `SELECT` (`GET`), update is `UPDATE` (`PUT` or `PATCH`), and delete is `DELETE` (`DELETE`). `WHERE` picks which rows a `SELECT`, `UPDATE` or `DELETE` touches. Always write the `WHERE` first: an `UPDATE` or `DELETE` without it changes every row in the table, which is a classic production accident.
