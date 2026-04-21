package com.personalbookcatalog.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Persistent book entity for the personal catalog.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String bookNameEn;

    @Column(length = 300)
    private String bookNameMr;

    @Column(nullable = false, length = 300)
    private String authorNameEn;

    @Column(length = 300)
    private String authorNameMr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ReadingStatus readingStatus;

    @Column
    private Integer rating;

    @Column(nullable = false, length = 120)
    private String genre;

    @Column(nullable = false, length = 120)
    private String bookLanguage;

    @Column(length = 120)
    private String location;

    @Column(length = 1000)
    private String customTags;

    @Column(length = 1000)
    private String customCategories;

    /**
     * Returns generated identifier of the book.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets generated identifier of the book.
     */
    public void setId(Long id) {
        this.id = id;
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
     * Returns book title in Marathi.
     */
    public String getBookNameMr() {
        return bookNameMr;
    }

    /**
     * Sets book title in Marathi.
     */
    public void setBookNameMr(String bookNameMr) {
        this.bookNameMr = bookNameMr;
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

    /**
     * Returns author name in Marathi.
     */
    public String getAuthorNameMr() {
        return authorNameMr;
    }

    /**
     * Sets author name in Marathi.
     */
    public void setAuthorNameMr(String authorNameMr) {
        this.authorNameMr = authorNameMr;
    }

    /**
     * Returns current reading status.
     */
    public ReadingStatus getReadingStatus() {
        return readingStatus;
    }

    /**
     * Sets current reading status.
     */
    public void setReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    /**
     * Returns rating value from 1 to 5, or null if unrated.
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Sets rating value from 1 to 5.
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * Returns primary genre label.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets primary genre label.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns primary book language label.
     */
    public String getBookLanguage() {
        return bookLanguage;
    }

    /**
     * Sets primary book language label.
     */
    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    /**
     * Returns shelf/location label for the book.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets shelf/location label for the book.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns normalized comma-separated tags.
     */
    public String getCustomTags() {
        return customTags;
    }

    /**
     * Sets normalized comma-separated tags.
     */
    public void setCustomTags(String customTags) {
        this.customTags = customTags;
    }

    /**
     * Returns normalized comma-separated categories.
     */
    public String getCustomCategories() {
        return customCategories;
    }

    /**
     * Sets normalized comma-separated categories.
     */
    public void setCustomCategories(String customCategories) {
        this.customCategories = customCategories;
    }
}
