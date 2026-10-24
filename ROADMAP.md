# Ticket Booking: Learning Roadmap

Goal: job-ready Java backend skills, built through an event ticket booking backend.
About 10 weeks at 10–20 h/week. Start applying after week 5 (marked ★ below) and keep building while you interview.

**Stack:** Java 21 · Spring Boot 4.1.x · Maven · PostgreSQL · Redis · Kafka · Docker · GitHub Actions · Jenkins · SonarQube · Kubernetes · Helm · AWS

**How we work on each feature:** explain the concept → sketch the design → Claude writes the scaffolding and tricky parts → you write the rest → code review → tests → you add the likely interview questions to `docs/interview-notes.md`.

---

## Week 1: Core Java refresher (no Spring yet)
**Build:** the ticketing domain in plain Java (`Venue`, `Seat`, `Event`, `Booking`) and an in-memory `BookingService` that holds and confirms seats.

Focus from the warm-up (2026-10-24): **[deep]** = learn properly · **[refresh]** = fix gaps · **[quick]** = already solid. Expect this week to take about 1.5 weeks; that's time well spent, since these are the most-asked interview topics.

- [ ] **[deep]** Collections: `List`/`Set`/`Map`, when to use which, how `HashMap` works, the `equals`/`hashCode` contract
- [ ] **[deep]** Records, enums, immutability
- [ ] **[deep]** Lambdas, functional interfaces, streams (`filter`/`map`/`reduce`/`collect`), `Optional`
- [ ] **[refresh]** OOP: interfaces vs abstract classes (state, default methods, multiple inheritance), composition over inheritance
- [ ] **[refresh]** Generics
- [ ] **[refresh]** Exceptions: checked vs unchecked (*compiler*-checked, not "compile-time"), custom exceptions
- [ ] **[quick]** `==` vs `equals`, the String pool
- [ ] Modern Java (17–21): switch expressions, pattern matching, sealed types, text blocks
- [ ] Tests: JUnit + AssertJ for the domain logic
- [x] Tools: JDK 21, Maven, Maven wrapper, `.gitignore`
- [ ] Git beyond the basics: feature branches, pull requests, rebase vs merge; GitHub repo

**Interview:** HashMap internals · equals/hashCode contract · why String is immutable · `==` vs `equals` · checked vs unchecked exceptions · intermediate vs terminal stream operations

## Week 2: Spring Boot core + REST APIs
**Build:** wire the week 1 classes together by hand, then let Spring do it. REST APIs for venues, events and seats, with in-memory storage for now.

- [ ] **[deep]** Inversion of control and dependency injection (constructor injection), beans, scopes, lifecycle
- [ ] **[deep]** `@Component` vs `@Bean`, and how Spring finds and creates beans
- [ ] Starters and auto-configuration (what `@SpringBootApplication` actually does)
- [ ] `application.yml`, profiles, `@ConfigurationProperties`
- [ ] Spring MVC request flow (`DispatcherServlet`), `@RestController`, HTTP status codes, REST design
- [ ] DTOs as records, Bean Validation, global error handling with `ProblemDetail`
- [ ] Logging with SLF4J
- [ ] Tests: `@WebMvcTest` + Mockito
- [ ] Tools: Spring Initializr, Swagger UI (springdoc-openapi), Postman

**Interview:** `@Component` vs `@Bean` · constructor vs field injection · how auto-configuration works · bean scopes · idempotent HTTP methods and status codes

## Week 3: Databases + Docker
**Build:** replace the in-memory storage with PostgreSQL.

- [ ] SQL basics: `SELECT`, `JOIN`, `GROUP BY`, primary/foreign keys, constraints, indexes
- [ ] Docker basics: images, containers, volumes, ports; Docker Compose
- [ ] PostgreSQL in Docker, using Spring Boot's Docker Compose support
- [ ] Spring Data JPA + Hibernate: entities, relationships, fetch types, the N+1 problem, pagination and sorting
- [ ] Flyway database migrations
- [ ] How Spring Data generates repository implementations from interfaces (proxies)
- [ ] Tests: `@DataJpaTest` + Testcontainers

**Interview:** JDBC vs JPA vs Hibernate · lazy vs eager loading · fixing N+1 · what an index does · SQL join types

## Week 4: Booking flow + concurrency
**Build:** hold seats → confirm booking, and make double booking impossible.

- [ ] **[deep]** Java concurrency: threads, `ExecutorService`, race conditions (why `count++` isn't safe), `synchronized` and locks, `ConcurrentHashMap`, `CompletableFuture`, virtual threads
- [ ] Reproduce a double booking with 50 threads in plain Java, then fix it
- [ ] `@Transactional`: propagation, isolation, rollback rules, and why calling it from the same class skips it
- [ ] Optimistic locking (`@Version`) vs pessimistic locking (`SELECT … FOR UPDATE`)
- [ ] Tests: concurrent booking integration test with Testcontainers

**Interview:** "How do you stop two users booking the same seat?" · `synchronized` vs database locks when running several app instances · isolation levels · virtual vs platform threads

## Week 5: Security
**Build:** register and login, JWT tokens, ADMIN and USER roles, users see only their own bookings.

- [ ] Password hashing with BCrypt, issuing and validating JWTs
- [ ] The Spring Security filter chain, servlet filters, `SecurityContext` and `ThreadLocal`
- [ ] Method-level security (`@PreAuthorize`)
- [ ] CORS and CSRF
- [ ] Tests: security tests with MockMvc

**Interview:** authentication vs authorization · JWT vs sessions · how the filter chain works · how to store passwords safely

### ★ Core stack done: start applying

## Week 6: Caching, messaging, second service
**Build:** cached event listings, seat holds that expire on their own, and a `notification-service` that emails tickets.

- [ ] Redis + Spring Cache for event listings, cache invalidation
- [ ] Seat holds in Redis with a TTL, plus an `@Scheduled` cleanup job
- [ ] Kafka basics: topics, partitions, consumer groups; Spring Kafka
- [ ] `BookingConfirmed` event → `notification-service` → email via Mailpit
- [ ] Spring AOP and proxies: how `@Cacheable` and `@Transactional` work under the hood
- [ ] JSON serialization with Jackson

**Interview:** cache-aside pattern · at-least-once delivery · idempotent consumers · monolith vs microservices · Kafka vs RabbitMQ

## Week 7: Resilience + observability
**Build:** a fake payment provider that sometimes fails, and dashboards showing how the system behaves.

- [ ] Calling other services with `RestClient` / HTTP interface clients
- [ ] Resilience4j: retry, circuit breaker, timeouts; idempotency keys for payments
- [ ] Actuator, Micrometer, Prometheus, Grafana dashboards
- [ ] Distributed tracing (Micrometer Tracing + Zipkin), structured logging

**Interview:** circuit breaker states · how you'd debug a slow endpoint in production · idempotency

## Week 8: CI/CD
- [ ] Multi-stage Dockerfile for both services
- [ ] GitHub Actions: build → test → SonarQube → push image to AWS ECR
- [ ] The same pipeline in Jenkins (`Jenkinsfile`, Jenkins running locally in Docker)
- [ ] AWS account setup + billing alarm

**Interview:** CI vs CD · Jenkins vs GitHub Actions · quality gates

## Week 9: Kubernetes + AWS
- [ ] Local cluster with kind: Deployments, Services, ConfigMaps, Secrets
- [ ] Liveness/readiness probes via Actuator, rolling updates, Horizontal Pod Autoscaler, Ingress
- [ ] The JVM in containers: heap sizing, memory limits, garbage collection basics
- [ ] Package as a Helm chart
- [ ] Deploy to EKS (eksctl) + RDS PostgreSQL, record a demo, tear it down the same day

**Interview:** pod vs deployment · how probes work · Kubernetes service discovery vs Eureka · JVM memory in containers

## Week 10: Polish + interview prep
- [ ] README with architecture diagram and setup instructions
- [ ] k6 load test proving no seat is ever double-booked
- [ ] Practise explaining the project as a system-design answer
- [ ] Mock interviews: core Java, Spring, SQL, system design

---

## If time allows (1–3 h each)
Terraform · Argo CD · Keycloak · MongoDB · Linux and shell basics

## Be able to explain in a sentence or two
Spring Cloud (Eureka, Config Server, Gateway) · Nexus/Artifactory · Ansible · Splunk/Datadog/ELK · GraphQL · gRPC
