package com.personalbookcatalog.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents a single book candidate inside a wishlist.
 */
@Entity
@Table(
        name = "wishlist_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wishlist_item_book_author",
                columnNames = {"wishlist_id", "book_name_en", "author_name_en"}))
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(name = "book_name_en", nullable = false, length = 300)
    private String bookNameEn;

    @Column(name = "author_name_en", nullable = false, length = 300)
    private String authorNameEn;

    /**
     * Returns wishlist item identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets wishlist item identifier.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns owning wishlist.
     */
    public Wishlist getWishlist() {
        return wishlist;
    }

    /**
     * Sets owning wishlist.
     */
    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    /**
     * Returns book title in English.
     */
    public String getBookNameEn() {
        return bookNameEn;
    }

    /**
     * Sets book title in English.
     */
    public void setBookNameEn(String bookNameEn) {
        this.bookNameEn = bookNameEn;
    }

    /**
     * Returns author name in English.
     */
    public String getAuthorNameEn() {
        return authorNameEn;
    }

    /**
     * Sets author name in English.
     */
    public void setAuthorNameEn(String authorNameEn) {
        this.authorNameEn = authorNameEn;
    }
}
