package com.personalbookcatalog.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Book} persistence operations.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}
