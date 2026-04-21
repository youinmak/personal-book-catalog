package com.personalbookcatalog.catalog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Represents a named wishlist containing desired books.
 */
@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> items = new ArrayList<>();

    /**
     * Initializes creation timestamp for new rows.
     */
    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Returns wishlist identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets wishlist identifier.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns wishlist display name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets wishlist display name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns wishlist creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets wishlist creation timestamp.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns wishlist items.
     */
    public List<WishlistItem> getItems() {
        return items;
    }

    /**
     * Sets wishlist items.
     */
    public void setItems(List<WishlistItem> items) {
        this.items = items;
    }
}
