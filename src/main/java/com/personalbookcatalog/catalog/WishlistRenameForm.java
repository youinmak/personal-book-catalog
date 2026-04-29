package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form model for renaming a wishlist.
 */
public class WishlistRenameForm {

    private static final String NO_ANGLE_BRACKETS = "^[^<>]*$";

    @NotBlank(message = "Wishlist name is required.")
    @Size(max = 120, message = "Wishlist name must be at most 120 characters.")
    @Pattern(regexp = NO_ANGLE_BRACKETS, message = "Wishlist name contains unsafe characters.")
    private String name;

    /**
     * Returns new wishlist name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new wishlist name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
