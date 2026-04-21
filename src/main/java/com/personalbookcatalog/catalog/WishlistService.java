package com.personalbookcatalog.catalog;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Handles wishlist lifecycle and wishlist item operations.
 */
@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    /**
     * Creates service with required repositories.
     */
    public WishlistService(WishlistRepository wishlistRepository, WishlistItemRepository wishlistItemRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
    }

    /**
     * Returns all wishlists ordered by id.
     */
    @Transactional(readOnly = true)
    public List<Wishlist> findAllWishlists() {
        return wishlistRepository.findAllByOrderByIdAsc();
    }

    /**
     * Returns active wishlist by requested id or first available.
     */
    @Transactional(readOnly = true)
    public Wishlist getActiveWishlist(Long requestedId) {
        List<Wishlist> wishlists = findAllWishlists();
        if (wishlists.isEmpty()) {
            return null;
        }
        Wishlist active = requestedId == null
                ? wishlists.getFirst()
                : wishlists.stream()
                .filter(wishlist -> wishlist.getId().equals(requestedId))
                .findFirst()
                .orElse(wishlists.getFirst());
        active.setItems(wishlistItemRepository.findAllByWishlistIdOrderByIdAsc(active.getId()));
        return active;
    }

    /**
     * Creates a new wishlist with unique name.
     */
    @Transactional
    public Wishlist createWishlist(String name) {
        String normalizedName = normalizeRequired(name, "Wishlist name is required.");
        if (wishlistRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Wishlist name already exists.");
        }
        Wishlist wishlist = new Wishlist();
        wishlist.setName(normalizedName);
        return wishlistRepository.save(wishlist);
    }

    /**
     * Renames an existing wishlist while preserving uniqueness.
     */
    @Transactional
    public Wishlist renameWishlist(Long id, String name) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Wishlist not found for id: " + id));
        String normalizedName = normalizeRequired(name, "Wishlist name is required.");
        wishlistRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Wishlist name already exists.");
                });
        wishlist.setName(normalizedName);
        return wishlistRepository.save(wishlist);
    }

    /**
     * Deletes wishlist and returns next active wishlist id if available.
     */
    @Transactional
    public Long deleteWishlist(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new NoSuchElementException("Wishlist not found for id: " + id);
        }
        wishlistRepository.deleteById(id);
        return findAllWishlists().stream().findFirst().map(Wishlist::getId).orElse(null);
    }

    /**
     * Adds a book-author entry to a wishlist.
     */
    @Transactional
    public WishlistItem addItem(Long wishlistId, String bookNameEn, String authorNameEn) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new NoSuchElementException("Wishlist not found for id: " + wishlistId));

        String normalizedBookName = normalizeRequired(bookNameEn, "Book name (English) is required.");
        String normalizedAuthorName = normalizeRequired(authorNameEn, "Author name (English) is required.");

        if (wishlistItemRepository.existsByWishlistIdAndBookNameEnIgnoreCaseAndAuthorNameEnIgnoreCase(
                wishlistId, normalizedBookName, normalizedAuthorName)) {
            throw new IllegalArgumentException("Book already exists in this wishlist.");
        }

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setBookNameEn(normalizedBookName);
        item.setAuthorNameEn(normalizedAuthorName);
        return wishlistItemRepository.save(item);
    }

    /**
     * Deletes an item from a specific wishlist.
     */
    @Transactional
    public void deleteItem(Long wishlistId, Long itemId) {
        WishlistItem item = wishlistItemRepository.findByIdAndWishlistId(itemId, wishlistId)
                .orElseThrow(() -> new NoSuchElementException("Wishlist item not found."));
        wishlistItemRepository.delete(item);
    }

    /**
     * Ensures required text input is present and trimmed.
     */
    private String normalizeRequired(String value, String errorMessage) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }
}
