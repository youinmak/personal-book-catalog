# Personal Book Catalog

Spring Boot + Thymeleaf personal books catalog with bilingual fields (English + Marathi) and persistent H2 file database.

## Requirements

- Java 21
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

Open `http://localhost:8080/books`.

## Persistence

The database is configured as H2 file storage in the repository:

- `jdbc:h2:file:./data/booksdb;AUTO_SERVER=TRUE`

Data survives application restarts because it is stored in files under `data/`.

## Features

- Add book
- Update existing book
- Delete book
- Store English and Marathi names for both Book and Author
- English fields required, Marathi fields optional
- UI localization with English/Marathi language toggle (top-right)

## Localization

- Default locale: English
- Switch locale using top-right toggle (`EN` / `मराठी`)
- Locale is stored in session (query parameter `?lang=en` or `?lang=mr`)

## Authorization

- Anonymous users: view/search/paginate only (read-only mode)
- Authorized `ADMIN` users: add/update/delete books and manage wishlists

### Admin credentials

Set these environment variables before starting the app:

```bash
export APP_AUTH_ADMIN_USERNAME=admin
export APP_AUTH_ADMIN_PASSWORD_HASH='$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
```

Open `/login` and sign in with your configured admin account.

Note: `APP_AUTH_ADMIN_PASSWORD_HASH` must be a BCrypt hash, not plaintext.
