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
