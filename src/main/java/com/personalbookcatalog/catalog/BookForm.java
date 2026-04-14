package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form-backing object for create and update operations on books.
 */
public class BookForm {

    private Long id;

    @NotBlank(message = "Book name (English) is required.")
    @Size(max = 300, message = "Book name (English) must be at most 300 characters.")
    private String bookNameEn;

    @Size(max = 300, message = "Book name (Marathi) must be at most 300 characters.")
    private String bookNameMr;

    @NotBlank(message = "Author name (English) is required.")
    @Size(max = 300, message = "Author name (English) must be at most 300 characters.")
    private String authorNameEn;

    @Size(max = 300, message = "Author name (Marathi) must be at most 300 characters.")
    private String authorNameMr;

    private ReadingStatus readingStatus;

    @Min(value = 1, message = "Rating must be between 1 and 5.")
    @Max(value = 5, message = "Rating must be between 1 and 5.")
    private Integer rating;

    @Size(max = 120, message = "Genre must be at most 120 characters.")
    private String genre;

    @Size(max = 120, message = "Book language must be at most 120 characters.")
    private String bookLanguage;

    @Size(max = 1000, message = "Custom tags must be at most 1000 characters.")
    private String customTags;

    @Size(max = 1000, message = "Custom categories must be at most 1000 characters.")
    private String customCategories;

    /**
     * Returns identifier for update context.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets identifier for update context.
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
     * Returns selected reading status.
     */
    public ReadingStatus getReadingStatus() {
        return readingStatus;
    }

    /**
     * Sets selected reading status.
     */
    public void setReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    /**
     * Returns optional rating value.
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Sets optional rating value.
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * Returns book genre.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets book genre.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns book language.
     */
    public String getBookLanguage() {
        return bookLanguage;
    }

    /**
     * Sets book language.
     */
    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    /**
     * Returns raw tags input.
     */
    public String getCustomTags() {
        return customTags;
    }

    /**
     * Sets raw tags input.
     */
    public void setCustomTags(String customTags) {
        this.customTags = customTags;
    }

    /**
     * Returns raw categories input.
     */
    public String getCustomCategories() {
        return customCategories;
    }

    /**
     * Sets raw categories input.
     */
    public void setCustomCategories(String customCategories) {
        this.customCategories = customCategories;
    }
}
