package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form-backing object for create and update operations on books.
 */
public class BookForm {

    private static final String NO_ANGLE_BRACKETS = "^[^<>]*$";

    private Long id;

    @NotBlank(message = "Book name (English) is required.")
    @Size(max = 300, message = "Book name (English) must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Book name (English) contains unsafe characters.")
    private String bookNameEn;

    @Size(max = 300, message = "Book name (Marathi) must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Book name (Marathi) contains unsafe characters.")
    private String bookNameMr;

    @NotBlank(message = "Author name (English) is required.")
    @Size(max = 300, message = "Author name (English) must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Author name (English) contains unsafe characters.")
    private String authorNameEn;

    @Size(max = 300, message = "Author name (Marathi) must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Author name (Marathi) contains unsafe characters.")
    private String authorNameMr;

    private ReadingStatus readingStatus;

    @Size(max = 120, message = "Book language must be at most 120 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Book language contains unsafe characters.")
    private String bookLanguage;

    @Size(max = 120, message = "Location must be at most 120 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Location contains unsafe characters.")
    private String location;

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
     * Returns location label.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets location label.
     */
    public void setLocation(String location) {
        this.location = location;
    }

}
