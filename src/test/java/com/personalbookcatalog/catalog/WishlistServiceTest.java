package com.personalbookcatalog.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void createWishlist_shouldRejectDuplicateName() {
        when(wishlistRepository.existsByNameIgnoreCase("Favorites")).thenReturn(true);

        assertThatThrownBy(() -> wishlistService.createWishlist("Favorites"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void renameWishlist_shouldRejectBlankName() {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(1L);
        when(wishlistRepository.findById(1L)).thenReturn(Optional.of(wishlist));

        assertThatThrownBy(() -> wishlistService.renameWishlist(1L, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void addItem_shouldRejectDuplicatesInSameWishlist() {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(2L);
        when(wishlistRepository.findById(2L)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.existsByWishlistIdAndBookNameEnIgnoreCaseAndAuthorNameEnIgnoreCase(
                2L, "Clean Code", "Robert Martin")).thenReturn(true);

        assertThatThrownBy(() -> wishlistService.addItem(2L, "Clean Code", "Robert Martin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void deleteWishlist_shouldReturnFirstRemainingId() {
        Wishlist remaining = new Wishlist();
        remaining.setId(7L);
        when(wishlistRepository.existsById(3L)).thenReturn(true);
        when(wishlistRepository.findAllByOrderByIdAsc()).thenReturn(List.of(remaining));

        Long nextId = wishlistService.deleteWishlist(3L);

        assertThat(nextId).isEqualTo(7L);
        verify(wishlistRepository).deleteById(3L);
    }

    @Test
    void deleteItem_shouldThrowWhenNotFound() {
        when(wishlistItemRepository.findByIdAndWishlistId(11L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistService.deleteItem(5L, 11L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void createWishlist_shouldTrimAndPersist() {
        Wishlist wishlist = new Wishlist();
        wishlist.setName("Reading List");
        when(wishlistRepository.existsByNameIgnoreCase("Reading List")).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wishlist saved = wishlistService.createWishlist(" Reading List ");

        assertThat(saved.getName()).isEqualTo("Reading List");
        verify(wishlistRepository).save(any(Wishlist.class));
    }
}
