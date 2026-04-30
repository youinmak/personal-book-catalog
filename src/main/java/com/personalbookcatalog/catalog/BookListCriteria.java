package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Encapsulates all list-page search, filter, and sorting inputs.
 */
public class BookListCriteria {

    private static final String NO_ANGLE_BRACKETS = "^[^<>]*$";

    @Size(max = 300, message = "Search query must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Search query contains unsafe characters.")
    private String query;
    private ReadingStatus readingStatus;

    @Size(max = 120, message = "Language filter must be at most 120 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Language filter contains unsafe characters.")
    private String bookLanguage;

    @Pattern(
            regexp = "^(id|authorName|bookName|readingStatus)?$",
            message = "Invalid sort field.")
    private String sortBy;

    @Pattern(regexp = "^(asc|desc)?$", message = "Invalid sort direction.")
    private String sortDir;

    /**
     * Returns free-text query for broad matching across book fields.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets free-text query for broad matching across book fields.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Returns requested reading status filter.
     */
    public ReadingStatus getReadingStatus() {
        return readingStatus;
    }

    /**
     * Sets requested reading status filter.
     */
    public void setReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    /**
     * Returns requested book language filter.
     */
    public String getBookLanguage() {
        return bookLanguage;
    }

    /**
     * Sets requested book language filter.
     */
    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    /**
     * Returns selected sort field identifier.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets selected sort field identifier.
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Returns selected sort direction identifier.
     */
    public String getSortDir() {
        return sortDir;
    }

    /**
     * Sets selected sort direction identifier.
     */
    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
