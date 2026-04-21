package com.personalbookcatalog.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for wishlist persistence operations.
 */
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Returns all wishlists ordered by creation id.
     */
    List<Wishlist> findAllByOrderByIdAsc();

    /**
     * Checks whether a wishlist name already exists, case-insensitive.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds a wishlist by name ignoring case.
     */
    Optional<Wishlist> findByNameIgnoreCase(String name);
}
