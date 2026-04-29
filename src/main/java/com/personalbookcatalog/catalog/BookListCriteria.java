package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(value = 1, message = "Minimum rating must be between 1 and 5.")
    @Max(value = 5, message = "Minimum rating must be between 1 and 5.")
    private Integer minRating;

    @Size(max = 120, message = "Genre filter must be at most 120 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Genre filter contains unsafe characters.")
    private String genre;

    @Size(max = 120, message = "Language filter must be at most 120 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Language filter contains unsafe characters.")
    private String bookLanguage;

    @Size(max = 500, message = "Tags filter must be at most 500 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Tags filter contains unsafe characters.")
    private String tags;

    @Size(max = 500, message = "Categories filter must be at most 500 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Categories filter contains unsafe characters.")
    private String categories;

    @Pattern(
            regexp = "^(id|authorName|bookName|readingStatus|rating|genre)?$",
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
     * Returns minimum allowed rating for list filtering.
     */
    public Integer getMinRating() {
        return minRating;
    }

    /**
     * Sets minimum allowed rating for list filtering.
     */
    public void setMinRating(Integer minRating) {
        this.minRating = minRating;
    }

    /**
     * Returns requested genre filter.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets requested genre filter.
     */
    public void setGenre(String genre) {
        this.genre = genre;
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
     * Returns comma-separated tags requested for filtering.
     */
    public String getTags() {
        return tags;
    }

    /**
     * Sets comma-separated tags requested for filtering.
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Returns comma-separated categories requested for filtering.
     */
    public String getCategories() {
        return categories;
    }

    /**
     * Sets comma-separated categories requested for filtering.
     */
    public void setCategories(String categories) {
        this.categories = categories;
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
