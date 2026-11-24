# How it all works: the tools in our farmer onboarding system

Simple explanations, starting from zero. For each tool: **what it is**, **why we need it**, and **where it fits in our project**.

---

## The big picture: one request's journey

A field officer taps "Submit" in the mobile app. Here's what happens:

```
 Mobile app / Angular admin screens
            │  HTTPS request
            ▼
 nginx                    ← the public front door on our server
            │
            ▼
 api-gateway              ← checks the login token, picks the right service
            │
            ▼
 onboarding-service       ← our Spring Boot code: checks the rules, saves the data
            │                     │
            ▼                     ▼
 PostgreSQL               Kafka   ← "application submitted" message
 (the database)                   │
                                  ▼
                        notification-service → Firebase → push notification on the RM's phone
```

Everything runs inside **Docker containers**. On your laptop, **Docker Compose** starts them. On the server, **Kubernetes** runs them. **Jenkins** builds and deploys new versions automatically, and **Prometheus + Grafana** watch that everything stays healthy.

The rest of this page explains each box.

---

## Part 1: the tools we're using now

### 1. Maven: builds the Java project
- **What:** a build tool. You tell it what your project needs, and it does the work:
  - downloads the libraries
  - compiles your `.java` files into `.class` files
  - runs the tests
  - packs everything into a **jar** (a zip of compiled code you can run with `java -jar`)
- **Why:** without it, you'd download dozens of libraries by hand, and compile and package manually.
- **`pom.xml`** is Maven's recipe: the project's name, its libraries (dependencies), and its settings.
- **`./mvnw`** (the Maven wrapper) runs the right Maven version without installing it.
- **In our project:**
  - `farmer-onboarding/pom.xml` is the **parent**. It holds the shared settings and lists the services (modules). It has no code, so it makes no jar.
  - Each service, like `onboarding-service`, has its own `pom.xml` and becomes its own jar.

### 2. Spring Boot: the framework our services are built on
- **What:** a Java framework that does the plumbing for you: a web server, JSON conversion, database connections, security, configuration.
- **Why:** you write only the business logic, like "an RM can approve a submitted application". Spring Boot handles the rest.
- **Key ideas you've learned:**
  - **Starters:** one dependency brings a whole set of libraries.
  - **Auto-configuration:** Spring Boot sees what's on the classpath and sets it up.
  - **Beans and dependency injection:** Spring creates objects and hands them to whoever needs them.
  - **Layers:** controller → service → repository.
- **`application.yml`** is the settings file: the port, the database address, and so on.

### 3. A database, and PostgreSQL
- **What a database is:** permanent, organised storage. Data is kept in **tables**, like Excel sheets:
  - Each **column** is a field: name, mobile, status.
  - Each **row** is one record: one farmer application.
- **Why not keep data in Java memory** (like ticket-booking's `ConcurrentHashMap`)?
  1. Memory is wiped when the app restarts. A database keeps data on disk.
  2. Many services and users can read and write at the same time, safely.
  3. Searching is fast, even across millions of rows, thanks to **indexes**.
  4. **Transactions:** a group of changes either all happen or none do. No half-saved applications.
- **SQL** is the language for talking to databases: `SELECT * FROM farmer_application WHERE status = 2`.
- **PostgreSQL** is a free, very popular database used by many MNCs. Other databases you'll hear about are MySQL, Oracle and SQL Server.
- **In our project:** each microservice gets **its own database**. Services never read each other's tables. They ask each other through APIs or Kafka.
- **Looking inside: `psql`**, PostgreSQL's own command-line tool. It's already inside the PostgreSQL container, so there's nothing to install.
  - **Open it** (from the `farmer-onboarding` folder): `docker compose exec postgres psql -U onboarding -d onboarding`. `exec` runs a program inside a running container, `-U` is the user and `-d` is the database. Inside the container, no password is asked.
  - **The prompt `onboarding=#`:** the database name, and `#` for an admin user (`=>` for a normal one). If it changes to `onboarding-#`, psql is waiting for a `;`.
  - **Shortcuts** (no `;` needed): `\dt` lists the tables, `\d farmer_application` shows a table's columns and rules, and `\q` quits. If you see `(END)`, press `q`.
  - **Basic SQL** (ends with `;`). These map to CRUD and to REST:

    | SQL | Does | REST |
    |---|---|---|
    | `INSERT INTO t (cols) VALUES (...)` | add a row | POST |
    | `SELECT cols FROM t WHERE ...` | read rows | GET |
    | `UPDATE t SET col = ... WHERE ...` | change rows | PUT/PATCH |
    | `DELETE FROM t WHERE ...` | remove rows | DELETE |

  - ⚠️ `UPDATE` or `DELETE` **without `WHERE`** changes **every row**. Write the `WHERE` first.
  - **Rules the database enforces (constraints):** `NOT NULL` refuses a row with a missing value. `PRIMARY KEY` means unique and not null, and PostgreSQL builds an **index** for it (a sorted B-tree, like a phone book), so finding a row by id is fast.
  - **pgAdmin** is the point-and-click version of psql. We'll add it later.

### 4. Docker: packs software so it runs the same everywhere
- **The problem:** installing PostgreSQL (or Kafka, or Redis) is different on a Mac, on Windows and on Linux. Versions clash, and "it works on my machine" but not on the server.
- **What Docker does:** it packs a piece of software together with everything it needs into one standard package. That package runs the same on your Mac, on your teammate's laptop and on the server.
- **The four words:**
  - **Image:** the package, like an installer. `postgres:17-alpine` is PostgreSQL 17, ready to run. You download it once.
  - **Container:** an image that's running. You can start, stop, delete and recreate containers in seconds.
  - **Volume:** storage that lives **outside** the container, like a locker. Delete the container and the data in the volume stays.
  - **Port mapping:** a container is like a sealed box. `127.0.0.1:5432:5432` opens a door from your Mac (port 5432) into the box (port 5432). `127.0.0.1` means only your Mac can use that door.
- **Why MNCs use it:** every service ships as an image. The same image goes through testing and into production, unchanged.
- **In our project:** PostgreSQL runs in Docker now. Later, Kafka, Redis, MinIO and our own services will too.

### 5. Docker Compose: starts several containers together
- **What:** one file, `compose.yaml`, lists all the containers a project needs, with their settings.
- **Why:** instead of typing a long `docker run ...` command for each tool, `docker compose up -d` starts everything, and `docker compose down` stops everything.
- **The everyday commands** (run them in the folder that has `compose.yaml`):
  - `docker compose up -d`: create and start the containers. `-d` means "in the background", so you get your terminal back.
  - `docker compose ps`: list them and show if they're **Up**.
  - `docker compose stop postgres` / `docker compose start postgres`: pause and resume one container. The container stays.
  - `docker compose down`: stop **and remove** the containers. Named volumes stay, so the data survives.
- **In our project:** `farmer-onboarding/compose.yaml` runs PostgreSQL on your laptop. Kafka, Redis and MinIO get added to it later. Compose is for **laptops and simple servers**. Big production setups use Kubernetes (Part 2).

### 6. JDBC, the driver, the datasource and the connection pool: how Java talks to a database
Think of a phone call:
- **JDBC** is Java's standard **phone system** for databases. The same Java code works with any database.
- The **driver** (the PostgreSQL JDBC driver) is the **translator**, turning JDBC calls into PostgreSQL's own language. Every database has its own driver.
- The **datasource** settings are the **phone number and password**: the URL `jdbc:postgresql://localhost:5432/onboarding`, the username and the password, all in `application.yml`.
- The **connection pool** (HikariCP) **keeps a few lines open** instead of dialling a new call for every request. Opening a database connection is slow, so reusing them makes the app much faster. Spring Boot sets this up automatically.
- **How to check it works:** start the service and look in the log for:
  - `HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection...`: the driver opened a real connection.
  - `HikariPool-1 - Start completed.`: the pool is ready.
  - Hibernate's `Database info:` block: it asked the database about itself (PostgreSQL 17.12) and picked the matching **dialect**.
- **If the database is down, the service refuses to start.** Hibernate must ask the database its type and version, so it knows which SQL "dialect" to write. With no database, it can't, and Spring stops the whole app. This is **fail fast**: better to fail at startup than on a real user's request.
- **Reading the error:** each layer wraps the error below it and adds its own message. Read the **last `Caused by:`**: that's the root cause. Here it was `java.net.ConnectException: Connection refused`, which means nothing was listening on port 5432.

### 7. JPA, Hibernate and Spring Data JPA: save Java objects without writing SQL
- **The problem:** our code works with Java objects (`FarmerApplication`), but the database stores rows. Converting back and forth by hand means writing a lot of SQL.
- **ORM (object-relational mapping)** does that conversion automatically: one Java class becomes one table, one object becomes one row, one field becomes one column.
- **JPA** is the Java **standard** (rules and annotations, like `@Entity`) for ORM.
- **Hibernate** is the most popular **implementation** of JPA, the library that actually does the work.
- **Spring Data JPA** goes one step further. You write only an interface, like `FarmerApplicationRepository`, and Spring writes the code behind `save`, `findById` and `findAll` for you.
- **In our project:** it replaces the in-memory repository from ticket-booking. Because of the layers, the controller and service barely notice.

### 8. Flyway: version control for the database structure
- **The problem:** the code changes over time (new table, new column), and the database structure must change with it, the same way, on every laptop and on the server.
- **What Flyway does:** you write numbered SQL files, like `V1__create_farmer_application.sql` and `V2__add_bank_details.sql`. When the service starts, Flyway runs only the files that haven't run yet on that database, in order, and records which ones ran.
- **Why MNCs use it:** database changes are reviewed in Git like code, and every environment ends up with exactly the same structure.
- **In our project:**
  - Two dependencies: `spring-boot-starter-flyway`, Spring Boot's automatic setup, and `flyway-database-postgresql`, Flyway's PostgreSQL add-on. Since Flyway 10, each database needs its own add-on.
  - Scripts live in `src/main/resources/db/migration/`. The name pattern is `V1__create_farmer_application.sql`: `V`, then the version, then **two** underscores, then a description.
  - Spring Boot runs Flyway at startup with the same datasource, **after** the connection pool starts and **before** Hibernate. The tables exist before Hibernate looks at them.
- **How it remembers:** its own table, `flyway_schema_history`, holds one row per script that ran: the version, the description, a **checksum** (a fingerprint of the file) and whether it succeeded.
- **The golden rule:** never edit a script that already ran. The checksum would change and Flyway would refuse to start. Add a new `V2__...` file instead.
- **On PostgreSQL, each script runs in a transaction.** If it fails halfway, all of its changes are undone.
- **How to check it works**, in the startup log:
  - First run: `Successfully applied 1 migration to schema "public", now at version v1`.
  - Every later run: `Schema "public" is up to date. No migration necessary.`
- **Harmless warning:** `Could not transfer metadata ... flyway-community-db-support` (or `MavenFeed`) `... 401`. Flyway's build files list extra download sites that need a login. Maven skips them and uses Maven Central.
- **Alternative you'll hear about:** Liquibase, which does the same job and also supports XML or YAML changelogs.

---

## Part 2: the tools coming later

### 9. Spring Security and JWT: login and permissions
- **Authentication** answers "who are you?" (login). **Authorization** answers "what are you allowed to do?" (an RM can approve; a field officer can't).
- A **JWT** is a **signed digital ID card**. After login, the server gives the app a token that holds the user's id and roles, with a signature nobody can fake. The app sends it with every request, so the server doesn't need to look up a session.
- **RBAC (role-based access control):** permissions are given to roles (RM, MDM...), and users get roles.

### 10. API gateway: the single front door for all services
- The mobile app calls **one** address. The gateway checks the token, then forwards each request to the right service: `/api/applications/**` goes to onboarding-service, `/api/auth/**` to auth-service.
- That keeps the internal services hidden, and one place handles login checks, rate limits and logging.

### 11. Kafka: services send messages without waiting for each other
- **The problem:** when an application is submitted, several things should happen: notify the RM, record an audit, maybe update a dashboard. If onboarding-service called each of those directly, it would slow down, and it would fail whenever one of them was down.
- **What Kafka is:** a **message log**, like a shared notice board. onboarding-service **publishes** "application 123 submitted" to a **topic**. Any service that cares **subscribes** and reacts in its own time. Messages are stored, so a service that was down catches up when it comes back.
- **Why MNCs use it:** services stay independent (loose coupling), and it handles millions of messages.

### 12. Redis: a very fast in-memory store (a cache)
- It keeps frequently read data, like the dropdown lists (regions, branches), in memory, so we don't query the database every time.
- It's also used for things that expire, like OTPs, which are valid for 5 minutes.

### 13. MinIO: storage for files (photos, PDFs)
- Databases are bad at storing big files. MinIO is an **object storage** server that works exactly like Amazon S3, but runs on our own server.
- The database keeps only the file's name and location. MinIO keeps the file itself.

### 14. Firebase Cloud Messaging (FCM): push notifications to phones
- Google's free service to send notifications to Android and iOS apps. Our notification-service tells FCM "send this message to this phone", and FCM delivers it.

### 15. Testing tools
- **JUnit:** runs tests. **AssertJ:** readable checks, like `assertThat(x).isEqualTo(y)`.
- **Mockito:** fake objects for unit tests, like a fake repository.
- **Testcontainers:** starts a real PostgreSQL (or Kafka) in Docker during tests, so tests run against the real thing.

### 16. Jenkins: the CI/CD robot
- **CI (continuous integration):** on every push to Git, a robot builds the code and runs all the tests, so mistakes are caught within minutes.
- **CD (continuous delivery/deployment):** if everything passes, the same robot builds the Docker images and deploys them to the server.
- **Jenkins** is the most common CI/CD tool in MNCs. A `Jenkinsfile` in the repo describes the steps (the **pipeline**). GitHub Actions and GitLab CI do the same job.

### 17. SonarQube: the code quality inspector
- It scans the code for bugs, security holes, duplicated code and missing tests. A **quality gate** can stop a bad build from being deployed.

### 18. Kubernetes (k3s) and Helm: run containers on servers, at scale
- Docker Compose starts containers. **Kubernetes** also **keeps them healthy**:
  - It restarts crashed services.
  - It runs several copies when traffic grows.
  - It rolls out new versions with no downtime.
  - It lets services find each other by name.
- **k3s** is a lightweight Kubernetes that fits our small server.
- **Helm** is an installer for Kubernetes apps. One package, a **chart**, describes everything a service needs.

### 19. nginx: the public web server in front
- It receives all internet traffic on ports 80 and 443, handles **HTTPS** (encryption, certificates), and passes requests to the right place inside the server (a **reverse proxy**).
- On our server, it's the only public door. Everything else listens only on `127.0.0.1`.

### 20. Prometheus, Grafana, logs and tracing: watching the system
- **Prometheus** collects numbers (**metrics**) from every service every few seconds: requests per second, errors, response time, memory.
- **Grafana** draws them as **dashboards**, and can send alerts.
- **Central logs** put every service's logs in one searchable place.
- **Tracing** follows one request across all the services, to find where it got slow.

### 21. Resilience4j: staying up when other systems fail
- Our backend calls outside systems (Aadhaar OTP, e-sign, SAP). When one is slow or down, we must not hang or crash:
  - **Timeouts:** give up after N seconds.
  - **Retries:** try again a few times.
  - **Circuit breaker:** after many failures, stop calling for a while and fail fast, then test again later.

### 22. Angular: the web admin screens
- A front-end framework for browser apps. The admin dashboard (review, approvals, reports) will call the same APIs as the mobile app.

---

## Part 3: how MNC teams use all this together

1. **On your laptop:** write code, run `docker compose up -d` for the databases and tools, and run the service from VS Code.
2. **Push to Git.** Jenkins builds and tests the code, SonarQube checks it, and Docker images are built.
3. **Deploy:** the images are deployed to Kubernetes on the server. Flyway updates the database structure on startup.
4. **Run:** nginx takes the traffic, Kubernetes keeps services healthy, and Prometheus and Grafana watch everything.
5. **Settings per environment** (laptop, test, production) live in **profiles** and **environment variables**. Passwords are never written in the code.
