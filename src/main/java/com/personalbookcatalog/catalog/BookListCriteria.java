package com.personalbookcatalog.catalog;

/**
 * Encapsulates all list-page search, filter, and sorting inputs.
 */
public class BookListCriteria {

    private String query;
    private ReadingStatus readingStatus;
    private Integer minRating;
    private String genre;
    private String bookLanguage;
    private String tags;
    private String categories;
    private String sortBy;
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
