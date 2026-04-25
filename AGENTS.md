# Repository Guidelines

## Project Structure & Module Organization
This is a Spring Boot + Thymeleaf app for personal book catalog management.

- `src/main/java/com/personalbookcatalog/catalog/`: domain, controller, services, repositories, bootstrap initializers.
- `src/main/resources/templates/books.html`: primary UI template.
- `src/main/resources/application.properties`: runtime configuration (H2 file DB, JPA settings).
- `src/test/java/com/personalbookcatalog/catalog/`: unit and integration tests.
- `src/test/resources/application-test.properties`: test-specific configuration.
- `data/`: persistent local artifacts (`BookList.csv`, H2 DB files, bootstrap marker files).

## Build, Test, and Development Commands
- `mvn clean test`: compile and run all tests.
- `mvn test`: run tests without cleaning.
- `mvn spring-boot:run`: start the app locally on `http://localhost:8080/books`.
- `mvn clean package`: build executable JAR in `target/`.

Use Java 21 and Maven 3.9+ (see `.java-version` and `pom.xml`).

## Coding Style & Naming Conventions
- Java style: 4-space indentation, no tabs, standard Spring Boot conventions.
- Class names: `PascalCase` (`BookService`, `WishlistBootstrapInitializer`).
- Methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Keep controller methods thin; move business logic to services.
- Prefer clear method-level Javadocs for public/service methods, especially non-trivial flows.

No dedicated formatter/linter is configured; use IDE Java formatting with consistent import order.

## Testing Guidelines
- Frameworks: JUnit 5 + Spring Boot Test.
- Naming: `*Test` suffix (`BookServiceTest`, `BookControllerIntegrationTest`).
- Add focused unit tests for service/repository logic and integration tests for controller flows.
- For targeted runs: `mvn -Dtest=BookServiceTest test`.

When changing bootstrap or CSV-import behavior, include restart/idempotency coverage.

## Commit & Pull Request Guidelines
Recent history shows mixed styles (`feat: ...`, `Added ...`, `first commit`). Standardize on short Conventional Commit style:

- `feat: add wishlist rename validation`
- `fix: prevent duplicate CSV bootstrap import`

PRs should include:
- Clear summary of behavior changes.
- Linked issue/task (if available).
- Test evidence (`mvn test` result or scoped command).
- UI screenshot/GIF when `books.html` is modified.

## Configuration & Data Notes
- H2 uses file persistence under `data/booksdb` so data survives restarts.
- Initial CSV load should be first-start only (marker-based). Preserve this behavior in migrations.
