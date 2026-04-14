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
