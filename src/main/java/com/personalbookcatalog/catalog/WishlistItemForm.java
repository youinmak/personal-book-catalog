package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form model for adding an item to a wishlist.
 */
public class WishlistItemForm {

    private static final String NO_ANGLE_BRACKETS = "^[^<>]*$";

    @NotBlank(message = "Book name (English) is required.")
    @Size(max = 300, message = "Book name (English) must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Book name (English) contains unsafe characters.")
    private String bookNameEn;

    @NotBlank(message = "Author name (English) is required.")
    @Size(max = 300, message = "Author name (English) must be at most 300 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Author name (English) contains unsafe characters.")
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
