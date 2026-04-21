package com.personalbookcatalog.catalog;

/**
 * Form model for adding an item to a wishlist.
 */
public class WishlistItemForm {

    private String bookNameEn;
    private String authorNameEn;

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
