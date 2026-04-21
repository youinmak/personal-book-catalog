package com.personalbookcatalog.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class WishlistBootstrapInitializerTest {

    @Test
    void run_shouldCreateDefaultWishlistWhenEmpty() throws Exception {
        WishlistRepository repository = org.mockito.Mockito.mock(WishlistRepository.class);
        when(repository.count()).thenReturn(0L);
        when(repository.save(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WishlistBootstrapInitializer initializer = new WishlistBootstrapInitializer(repository);
        initializer.run(null);

        verify(repository).save(any(Wishlist.class));
    }

    @Test
    void run_shouldSkipWhenWishlistsExist() throws Exception {
        WishlistRepository repository = org.mockito.Mockito.mock(WishlistRepository.class);
        when(repository.count()).thenReturn(2L);

        WishlistBootstrapInitializer initializer = new WishlistBootstrapInitializer(repository);
        initializer.run(null);

        verify(repository, never()).save(any(Wishlist.class));
    }
}
