# Ticket Booking: Learning Roadmap

Goal: interview-ready Java backend skills, practised on an event ticket booking system.
Topics are in the order interviewers ask about them most. Each stage takes a few sessions.

**Stack:** Java 21 · Spring Boot 4.1.x · Maven · PostgreSQL · pgAdmin · Redis · Kafka · Docker · Jenkins · GitHub Actions · GitLab CI · SonarQube · Kubernetes · Helm · GCP · Firebase · AWS · Angular

**How we work on each topic:**
1. Claude explains the concept in simple words.
2. Claude writes the code in the project. You run it and change one small thing to see it work.
3. Claude asks 5 likely interview questions. You answer, and Claude corrects.
4. The answers go into `docs/interview-notes.md`.

**Every session:** a 15–20 minute coding drill (Java, streams or SQL), solved without help. Tasks are tracked on a JIRA board.

Focus tags: **[deep]** = learn properly · **[refresh]** = fix gaps · **[quick]** = already solid.

---

## 1. Core Java (no Spring yet)
**Build:** the ticketing domain in plain Java (`Venue`, `Seat`, `Event`, `Booking`) and an in-memory `BookingService` that holds and confirms seats. Then break it with 50 threads, and fix it.

- [ ] **[deep]** Collections: `List`/`Set`/`Map`, when to use which, how `HashMap` works, the `equals`/`hashCode` contract
- [ ] **[deep]** Records, enums, immutability
- [ ] **[deep]** Lambdas, functional interfaces, streams (`filter`/`map`/`reduce`/`collect`), `Optional`
- [ ] **[refresh]** OOP: interfaces vs abstract classes (state, default methods, multiple inheritance), composition over inheritance
- [ ] **[refresh]** Generics
- [ ] **[refresh]** Exceptions: checked vs unchecked (*compiler*-checked, not "compile-time"), custom exceptions
- [ ] **[quick]** `==` vs `equals`, the String pool, why String is immutable
- [ ] Modern Java (17–21): switch expressions, pattern matching, sealed types, text blocks
- [ ] **[deep]** Concurrency: threads, `ExecutorService`, race conditions (why `count++` isn't safe), `synchronized` and locks, `ConcurrentHashMap`, `CompletableFuture`, virtual threads
- [ ] Reproduce a double booking with 50 threads in plain Java, then fix it
- [ ] JVM basics: heap vs stack, garbage collection, memory leaks
- [ ] Tests: JUnit + AssertJ for the domain logic
- [x] Tools: JDK 21, Maven, Maven wrapper, `.gitignore`
- [ ] Git beyond the basics: feature branches, pull requests, rebase vs merge; GitHub repo

**Interview:** HashMap internals · equals/hashCode contract · why String is immutable · `==` vs `equals` · checked vs unchecked exceptions · intermediate vs terminal stream operations · race conditions · `synchronized` vs `ConcurrentHashMap` · virtual vs platform threads

## 2. Spring Boot + REST APIs
**Build:** wire the stage 1 classes together by hand, then let Spring do it. REST APIs for venues, events and seats, with in-memory storage for now.

- [ ] **[deep]** Inversion of control and dependency injection (constructor injection), beans, scopes, lifecycle
- [ ] **[deep]** `@Component` vs `@Bean`, and how Spring finds and creates beans
- [ ] Starters and auto-configuration (what `@SpringBootApplication` actually does)
- [ ] `application.yml`, profiles, `@ConfigurationProperties`
- [ ] Spring MVC request flow (`DispatcherServlet`), `@RestController`, CRUD, HTTP status codes, REST design
- [ ] DTOs as records, Bean Validation, global error handling with `ProblemDetail`
- [ ] JSON serialization with Jackson
- [ ] Logging with SLF4J
- [ ] API testing: Swagger UI (springdoc-openapi) and a Postman collection
- [ ] Tests: `@WebMvcTest` + Mockito
- [ ] Tools: Spring Initializr

**Interview:** `@Component` vs `@Bean` · constructor vs field injection · how auto-configuration works · bean scopes · idempotent HTTP methods and status codes · PUT vs PATCH

## 3. SQL, JPA, Hibernate + Docker basics
**Build:** replace the in-memory storage with PostgreSQL, and make double booking impossible in the database.

- [ ] SQL: `SELECT`, `JOIN`, `GROUP BY`/`HAVING`, subqueries, primary/foreign keys, constraints, indexes
- [ ] Docker basics: images, containers, volumes, ports; Docker Compose
- [ ] PostgreSQL and pgAdmin in Docker Compose, using Spring Boot's Docker Compose support
- [ ] Spring Data JPA + Hibernate: entities, relationships, fetch types, the N+1 problem, pagination and sorting
- [ ] How Spring Data generates repository implementations from interfaces (proxies), and how Spring AOP makes `@Transactional` work
- [ ] Flyway database migrations
- [ ] `@Transactional`: propagation, isolation, rollback rules, and why calling it from the same class skips it
- [ ] Optimistic locking (`@Version`) vs pessimistic locking (`SELECT … FOR UPDATE`)
- [ ] Tests: `@DataJpaTest` + Testcontainers, including a concurrent booking test

**Interview:** JDBC vs JPA vs Hibernate · lazy vs eager loading · fixing N+1 · what an index does · SQL join types · isolation levels · "How do you stop two users booking the same seat?" · `synchronized` vs database locks when running several app instances

## 4. Security
**Build:** register and login, JWT tokens, ADMIN and USER roles, users see only their own bookings.

- [ ] Password hashing with BCrypt; issuing and validating JWTs; token expiry and refresh tokens
- [ ] The Spring Security filter chain, servlet filters, `SecurityContext` and `ThreadLocal`
- [ ] Role-based access control: roles vs authorities, method-level security (`@PreAuthorize`)
- [ ] CORS and CSRF
- [ ] Tests: security tests with MockMvc

**Interview:** authentication vs authorization · JWT vs sessions · how the filter chain works · how to store passwords safely · where the client should keep the token

## 5. Microservices + Kafka
**Build:** a `notification-service` that emails tickets, and a fake payment provider that sometimes fails.

- [ ] Monolith vs microservices, service boundaries, a database per service
- [ ] Kafka basics: topics, partitions, offsets, consumer groups; Spring Kafka
- [ ] `BookingConfirmed` event → `notification-service` → email via Mailpit
- [ ] At-least-once delivery, idempotent consumers, retries and dead-letter topics
- [ ] Calling other services with `RestClient` / HTTP interface clients
- [ ] Resilience4j: retry, circuit breaker, timeouts; idempotency keys for payments

**Interview:** monolith vs microservices · Kafka vs RabbitMQ · at-least-once delivery · idempotent consumers · circuit breaker states · idempotency

## 6. Docker + CI/CD
- [ ] Multi-stage Dockerfile for both services
- [ ] Jenkins pipeline (`Jenkinsfile`, Jenkins running locally in Docker): build → test → SonarQube → push image
- [ ] The same pipeline in GitHub Actions
- [ ] GitLab CI basics (`.gitlab-ci.yml`) on a GitLab mirror of the repo
- [ ] SonarQube quality gates

**Interview:** CI vs CD · Jenkins vs GitHub Actions vs GitLab CI · quality gates · Docker image layers · `CMD` vs `ENTRYPOINT`

## 7. Redis caching
- [ ] Redis + Spring Cache for event listings, cache invalidation
- [ ] Seat holds in Redis with a TTL, plus an `@Scheduled` cleanup job

**Interview:** cache-aside pattern · cache invalidation · TTL · Redis vs an in-process cache

## 8. Kubernetes + cloud
- [ ] Local cluster with kind: Deployments, Services, ConfigMaps, Secrets
- [ ] Liveness/readiness probes via Actuator, rolling updates, Horizontal Pod Autoscaler, Ingress
- [ ] The JVM in containers: heap sizing, memory limits
- [ ] Package as a Helm chart
- [ ] GCP: deploy to GKE or Cloud Run with Cloud SQL PostgreSQL, plus a small Cloud Function; billing alert; tear it down the same day
- [ ] AWS: the same app on EKS + RDS, image in ECR; billing alarm; tear it down the same day

**Interview:** pod vs deployment · how probes work · Kubernetes service discovery vs Eureka · JVM memory in containers · GKE vs Cloud Run · matching GCP and AWS services

## 9. Observability
- [ ] Actuator, Micrometer, Prometheus, Grafana dashboards
- [ ] Distributed tracing (Micrometer Tracing + Zipkin), structured logging

**Interview:** how you'd debug a slow endpoint in production · metrics vs logs vs traces

## 10. Angular frontend **[refresh]**
**Build:** an Angular app for the booking system: event list, seat picker, login, and an admin dashboard.

- [ ] TypeScript essentials, HTML and CSS basics
- [ ] Components, templates, data binding, standalone components, signals
- [ ] Services, dependency injection in Angular, `HttpClient` calling the Spring Boot APIs
- [ ] Routing, route guards, and an HTTP interceptor that adds the JWT
- [ ] RxJS basics: `Observable`, `subscribe`, `map`, `switchMap`
- [ ] Reactive forms and validation
- [ ] Admin dashboard: bookings with pagination, approve or cancel, simple reports
- [ ] Firebase Cloud Messaging: `notification-service` pushes "booking confirmed" to the browser

**Interview:** components vs services · how the interceptor adds the token · Observable vs Promise · change detection · why CORS errors happen

## 11. Polish + interview prep
- [ ] README with architecture diagram and setup instructions
- [ ] k6 load test proving no seat is ever double-booked
- [ ] Practise explaining the project as a system-design answer
- [ ] Mock interviews: core Java, Spring, SQL, microservices, system design

---

## If time allows (1–3 h each)
Terraform · Argo CD · Keycloak · MongoDB · Linux and shell basics

## Be able to explain in a sentence or two
Spring Cloud (Eureka, Config Server, Gateway) · Nexus/Artifactory · Ansible · Splunk/Datadog/ELK · GraphQL · gRPC
