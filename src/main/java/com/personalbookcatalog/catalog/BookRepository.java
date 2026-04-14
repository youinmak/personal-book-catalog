package com.personalbookcatalog.catalog;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(COALESCE(b.bookNameEn, '')) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(COALESCE(b.bookNameMr, '')) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(COALESCE(b.authorNameEn, '')) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(COALESCE(b.authorNameMr, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    List<Book> searchAcrossAllFields(@Param("query") String query, Sort sort);
}
