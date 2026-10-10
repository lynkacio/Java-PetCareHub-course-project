# PetCareHub

PetCareHub is a **Java 17 Swing desktop application** for coordinating pet owners, verified volunteer helpers, and administrators. It started as a university Java programming lab and was refactored into a more portfolio-ready project with a layered architecture, persistent storage, task lifecycle management, secure password handling, analytics, filtering/sorting, and automated smoke tests.

## Highlights

- **Role-based workflows** for pet owners, helpers, and administrators.
- **OOP domain model** using inheritance, polymorphism, enums, encapsulation, and composition.
- **Task lifecycle state machine**: `OPEN -> ASSIGNED -> COMPLETED`, with cancellation support.
- **Search + sorting** of available tasks using the Stream API, lambdas, and comparators.
- **Pet subtype modelling** for dogs, cats, and other pets with subtype-specific behaviour.
- **CSV persistence layer** behind a generic `Repository<T>` abstraction.
- **Atomic file writes** to reduce the risk of partially written data files.
- **PBKDF2-HMAC-SHA256 password hashing** with per-password random salts.
- **Backward-compatible password migration** for legacy plain-text records after successful login.
- **PDF helper-certificate upload and admin verification workflow**.
- **Admin analytics dashboard** with a custom-painted Swing bar chart.
- **CSV report export**, audit logging, input validation, and custom exceptions.
- **No runtime third-party libraries**: the application uses only the Java standard library.
- **GitHub Actions CI** plus dependency-free smoke tests.

## Demo Accounts

The application creates demo data automatically on first launch.

| Role | User ID | Password |
|---|---|---|
| Owner | `owner1` | `owner123` |
| Verified helper | `helper1` | `helper123` |
| Admin | `admin` | `admin123` |

## Run

### Windows

```bat
scripts\run.bat
```

### macOS / Linux

```bash
bash scripts/run.sh
```

Requirements: **JDK 17+**.

You can also open the project in IntelliJ IDEA or Eclipse and run:

```text
com.petcarehub.app.Main
```

## Test

```bash
bash scripts/test.sh
```

Windows: `scripts\test.bat`

The smoke test checks password hashing, task persistence/lifecycle, and pet polymorphism using temporary data directories.

## Build a runnable JAR

```bash
bash scripts/build.sh
java -jar dist/petcarehub.jar
```

Windows: `scripts\build.bat`

A Maven `pom.xml` is also included for IDE/project import and conventional Java project metadata.

## Architecture

```text
src/main/java/com/petcarehub/
├── app/          Application bootstrap, dependency wiring, demo seeding
├── model/        Domain entities and enums
├── repository/   CSV repositories and generic Repository<T> abstraction
├── service/      Authentication, task, pet, certificate, account and analytics logic
├── util/         Validation, CSV parsing, password hashing, atomic I/O, audit log
├── exception/    Domain-specific exceptions
└── ui/           Swing frames/panels and custom analytics chart
```

The UI depends on services rather than manipulating data files directly. Services implement business rules, while repositories isolate persistence concerns. This keeps the project small enough for a portfolio demo while still showing separation of concerns.

## Java Concepts Demonstrated

- Classes, abstract classes, interfaces, inheritance and polymorphism
- Generics (`Repository<T>`)
- Collections (`List`, `Map`, `EnumMap`)
- Streams, lambdas, method references and comparators
- Enums and state transitions
- Records (`AnalyticsService.Snapshot`)
- Exceptions and validation
- Java NIO (`Path`, `Files`, atomic moves)
- Cryptography APIs (`SecretKeyFactory`, `PBEKeySpec`, `SecureRandom`)
- Swing UI, event listeners and custom painting
- UUIDs and Java Time API
- File export and audit logging

## Main Workflows

### Pet Owner

1. Sign in or create an owner account.
2. Add/remove pets.
3. Publish care tasks with hours and hourly rates.
4. Track task status and cancel non-completed tasks.

### Helper

1. Create a helper account.
2. Upload a PDF volunteering certificate.
3. Wait for admin verification.
4. Search/sort open tasks and accept one.
5. Complete assigned work; completion improves the helper credit score.

### Admin

1. Review helper certificate status.
2. Approve or revoke helper verification.
3. View system-wide tasks and cancel tasks when needed.
4. Review live analytics and task-status distribution.
5. Export the task dataset as CSV.

## Data Storage

Runtime data is stored in the local `data/` folder:

- `users.csv`
- `pets.csv`
- `tasks.csv`
- `audit.log`
- `certificates/`

The files are intentionally human-readable so the persistence logic can be inspected without requiring a database server.

## Design Trade-offs

This project intentionally keeps persistence local and dependency-free. For a production deployment, the next logical upgrades would be PostgreSQL/JPA, a Spring Boot REST API, stronger authorization boundaries, integration tests, and a web/mobile client. For a desktop Java portfolio project, the current implementation keeps the focus on core Java and software design rather than framework setup.

## Resume-ready Description

> Built a role-based Java 17 Swing pet-care marketplace for owners, verified helpers, and administrators, implementing a layered service/repository architecture, polymorphic domain models, task lifecycle management, Stream-based search/sorting, CSV persistence with atomic writes, PBKDF2 password hashing, certificate verification, analytics, report export, audit logging, and automated CI smoke tests.

## License

MIT License.
