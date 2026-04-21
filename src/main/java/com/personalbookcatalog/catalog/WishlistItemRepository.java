package com.personalbookcatalog.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for wishlist item persistence operations.
 */
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    /**
     * Returns all items for a wishlist ordered by item id.
     */
    List<WishlistItem> findAllByWishlistIdOrderByIdAsc(Long wishlistId);

    /**
     * Checks whether a book-author pair already exists in a wishlist.
     */
    boolean existsByWishlistIdAndBookNameEnIgnoreCaseAndAuthorNameEnIgnoreCase(
            Long wishlistId, String bookNameEn, String authorNameEn);

    /**
     * Finds an item by id and owning wishlist id.
     */
    Optional<WishlistItem> findByIdAndWishlistId(Long id, Long wishlistId);
}
