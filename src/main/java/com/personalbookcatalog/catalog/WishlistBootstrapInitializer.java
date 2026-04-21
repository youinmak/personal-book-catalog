package com.personalbookcatalog.catalog;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Ensures at least one default wishlist exists.
 */
@Component
public class WishlistBootstrapInitializer implements ApplicationRunner {

    private final WishlistRepository wishlistRepository;

    /**
     * Creates initializer with repository dependency.
     */
    public WishlistBootstrapInitializer(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    /**
     * Creates a default wishlist when no wishlists exist.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (wishlistRepository.count() == 0) {
            Wishlist wishlist = new Wishlist();
            wishlist.setName("My Wishlist");
            wishlistRepository.save(wishlist);
        }
    }
}
