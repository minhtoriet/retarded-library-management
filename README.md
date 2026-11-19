# Library Management

In the shape of library management, this is where I apply supposedly "advanced" database elite ball knowledge or whatever I was told, including but not limited to:

- Query optimization
- Performance metrics
- Asynchronous and Multithreading (useless when the actual bottleneck lies in database pool)
- and more...

> **Status: half-baked skeleton.** A few endpoints work, a few pieces are written but not wired up, and some are empty files. Read [What to expect](#what-to-expect) before you run it so you don't file a bug against a feature that doesn't exist.

## Stack

| Thing | Version / note |
| --- | --- |
| Java | **25** (set in `pom.xml`, older JDKs will not compile it) |
| Spring Boot | 4.1.2 (Maven wrapper included, no need to install Maven) |
| Database | PostgreSQL (written against Neon, any Postgres works) |
| Libraries | Spring Web MVC, Spring Data JPA (Hibernate), Validation, Actuator, Micrometer Prometheus, springdoc OpenAPI, Lombok |
| Load testing | Apache JMeter (optional) |

## What to install

1. **JDK 25.** Check with `java -version`.
2. **A PostgreSQL database.** Pick one:
   - a [Neon](https://neon.tech) project (what this was built for), or
   - a local Postgres with an empty database.
3. **Apache JMeter** (optional, only for the `.jmx` files).
4. An IDE with Lombok support. In IntelliJ, enable annotation processing; VS Code's Java extension handles it.

You do **not** need to install Maven. Use `./mvnw` (or `mvnw.cmd` on Windows).

## Configuration

`src/main/resources/application.yml` reads the database settings from environment variables, and it will also import a `.env` file from the project root (`.env` is gitignored, so your credentials stay local).

Create `.env` in the repo root:

```properties
NEON_POOLED_URL=jdbc:postgresql://<host>/<database>?sslmode=require
NEON_USER=<user>
NEON_PASSWORD=<password>
```

Things that trip people up:

- The URL must be a **JDBC** URL (`jdbc:postgresql://...`). Neon's dashboard gives you `postgresql://user:pass@host/db`, which will not work as-is. Split out the user and password and add the `jdbc:` prefix.
- The variable is called `NEON_POOLED_URL` but nothing forces it to be Neon. For local Postgres use something like:
  ```properties
  NEON_POOLED_URL=jdbc:postgresql://localhost:5432/test
  NEON_USER=postgres
  NEON_PASSWORD=<your password>
  ```
- The app will **not start** without these three variables.
- `NEON_UNPOOLED_URL` is mentioned in the config as a comment for a future Flyway setup. It is not used today.

Other settings you'll find in the config: Hikari pool size is **5**, `open-in-view` is off, JDBC batch size is 100, and `ddl-auto` is `update` (Hibernate creates and alters the tables for you on startup; there are no migrations).

## How to run

```bash
git clone https://github.com/minhtoriet/retarded-library-management.git
cd retarded-library-management

# create .env as described above, then:
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080**.

On the first run against an empty database, `DataSeeder` inserts:

- 2000 students (`student1@example.com` ... `student2000@example.com`)
- 300 lecturers (`lecturer1@example.com` ... `lecturer300@example.com`)
- 2300 books (`Book 1` / `Author 1` ... `Book 2300` / `Author 2300`), each with 1 to 5 copies

The seeder skips itself if either the users or books table already has rows. To reseed, empty the tables yourself.

Useful URLs:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics

### Build and test

```bash
./mvnw clean package
```

The only test is `contextLoads`, which boots the whole app. That means it **needs a reachable database and the `.env` values** just to pass. There is no real test coverage.

## API

| Method | Path | What it does |
| --- | --- | --- |
| GET | `/api/books?page=0&size=20&sort=title,asc` | Paginated book list (id, title, author, available copies) |
| POST | `/api/borrow` | Borrow a book. Body: `{"userId": 1, "bookId": 1}`. Returns 201 |
| GET | `/api/user/{userId}/borrows` | Paginated borrow history for a user, newest first |
| GET | `/api/return` | Return a book. Body: `{"userId": 1, "bookId": 1}`. Yes, a GET with a request body, see below |

Example:

```bash
curl -X POST http://localhost:8080/api/borrow \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "bookId": 1}'
```

Seeded IDs come from Hibernate sequences, so check `/api/books` for real book IDs instead of assuming they start at 1.

## What to expect

### What works

- Book listing with pagination and sorting.
- Borrowing: checks that the user and book exist, that copies are available, and that the user isn't already holding that book; then decrements copies and records the borrow.
- Returning a book and restoring the copy count.
- Per-user borrow history using a DTO projection and a join (no entity loading, no N+1).
- Actuator health/metrics and Swagger docs.
- Seed data so there's something to query.

### Written but not reachable

- **Book search** (`BookRepository.search`) and **top borrowed books** (`findTopBorrowedBook`) exist as repository queries but **no controller exposes them**. You cannot call them over HTTP.
- `UserController` and `AppConfig` are empty.
- `UserRegisterRequestDto` and `UserLoginRequestDto` exist, but there is no registration, login or authentication of any kind. Anyone can borrow as any user.

### Known problems

- **`/api/return` is a `GET` with a request body.** Many clients and proxies drop bodies on GET, so it may not work outside curl/Swagger. It should be `POST` or `PUT`. Not fixed yet.
- **Borrowing is not concurrency-safe.** It reads `availableCopies`, checks it, then writes it back, with no row lock or `@Version`. Two simultaneous borrows of the last copy can both succeed and push the count negative. This is the most important thing to fix before trusting any "concurrent" benchmark.
- **The "optimization" is mostly not there yet.** There are no declared indexes. The search query uses `LOWER(col) LIKE '%kw%'`, which a normal B-tree index cannot serve, so it will scan the table. The DTO projections and join queries are the real wins so far.
- **Pool size of 5 is the bottleneck**, as the intro says. Adding threads in front of it just queues them.
- `micrometer-registry-prometheus` is on the classpath, but only `health`, `metrics` and `info` are exposed. There is no `/actuator/prometheus` until you add `prometheus` to `management.endpoints.web.exposure.include`.
- `pom.xml` pins `spring-boot-starter-validation` to `4.2.0-RC2` while the parent is `4.1.2`. A pre-release version mixed into a different Boot release is asking for trouble; if the build acts weird, remove the explicit version and let the parent manage it.
- There's a typo bug in the not-found message for books: it prints the **user** id instead of the book id.
- Seeded lecturers are all named `Student <n>` (copy-paste leftover).
- `application.properties` duplicates `ddl-auto` and `show-sql` settings, so SQL gets logged to the console on every request. Expect noisy logs.
- `ddl-auto: update` is fine for a demo and wrong for anything you care about.
- `jmeter.log` is committed to the repo. It shouldn't be.

## Load testing (optional)

Two JMeter plans are included. Both hit `GET http://localhost:8080/api/books?page=0&size=20`; **neither exercises borrowing**, so they say nothing about the race condition above.

| File | Notes |
| --- | --- |
| `concurrent-test.jmx` | Thread count is a property (default 10, override with `-Jthreads=N`), 10 s ramp-up, 50 loops per thread |
| `infinite-20threads.jmx` | Despite the name, it's configured for 65 threads. Runs until you stop it |

Run headless (the app must already be running):

```bash
jmeter -n -t concurrent-test.jmx -Jthreads=50 -l results.jtl
```

While it runs, watch `http://localhost:8080/actuator/metrics/hikaricp.connections.active` to see the pool saturate.

## Repo layout

```
src/main/java/com/lib/demo/
  controller/   REST endpoints (Book, Borrow; User is empty)
  service/      BorrowService (the only real business logic)
  repository/   Spring Data JPA repositories + JPQL projections
  model/        User (abstract, JOINED inheritance) -> Student, Lecturer; Book; BorrowRecord
  dto/          Request/response records and query projections
  config/       DataSeeder (AppConfig is empty)
  exception/    ApiExceptionHandler
resources/      Lab1-3.docx (course lab handouts)
*.jmx           JMeter test plans
```

## Roadmap (things that would make it less half-baked)

- [ ] Expose search and top-borrowed endpoints
- [ ] Fix `/api/return` verb
- [ ] Make borrowing concurrency-safe (pessimistic lock or optimistic `@Version`) and add a JMeter plan that actually borrows
- [ ] Add indexes and measure with `EXPLAIN ANALYZE` (trigram index for the `LIKE '%kw%'` search)
- [ ] Flyway migrations instead of `ddl-auto: update`
- [ ] Auth, or delete the unused login/register DTOs
- [ ] Real tests (Testcontainers instead of a live database)
