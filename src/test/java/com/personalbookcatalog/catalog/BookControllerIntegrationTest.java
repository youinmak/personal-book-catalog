package com.personalbookcatalog.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @BeforeEach
    void setUp() {
        wishlistItemRepository.deleteAll();
        wishlistRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void booksPage_shouldLoad() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Personal Book Catalog")))
                .andExpect(content().string(containsString("Wishlists")));
    }

    @Test
    void create_shouldPersistBookAndRedirect() throws Exception {
        mockMvc.perform(post("/books")
                        .param("bookNameEn", "Atomic Habits")
                        .param("bookNameMr", "अॅटोमिक हॅबिट्स")
                        .param("authorNameEn", "James Clear")
                        .param("authorNameMr", "जेम्स क्लिअर"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.count()).isEqualTo(1);
    }

    @Test
    void create_shouldRejectWhenEnglishFieldsMissing() throws Exception {
        mockMvc.perform(post("/books")
                        .param("bookNameEn", "")
                        .param("authorNameEn", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.count()).isZero();
    }

    @Test
    void create_shouldRejectUnsafeHtmlInput() throws Exception {
        mockMvc.perform(post("/books")
                        .param("bookNameEn", "<script>alert(1)</script>")
                        .param("authorNameEn", "Safe Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.count()).isZero();
    }

    @Test
    void update_shouldModifyExistingBook() throws Exception {
        Book book = new Book();
        book.setBookNameEn("Old Name");
        book.setAuthorNameEn("Old Author");
        book.setReadingStatus(ReadingStatus.UNREAD);
        book.setGenre("Unknown");
        book.setBookLanguage("Unknown");
        book = bookRepository.save(book);

        mockMvc.perform(post("/books/{id}/update", book.getId())
                        .param("bookNameEn", "New Name")
                        .param("bookNameMr", "नवीन नाव")
                        .param("authorNameEn", "New Author")
                        .param("authorNameMr", "नवीन लेखक")
                        .param("readingStatus", "FINISHED")
                        .param("rating", "4")
                        .param("genre", "Fiction")
                        .param("bookLanguage", "English")
                        .param("location", "Shelf A")
                        .param("customTags", "memoir")
                        .param("customCategories", "classics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(updated.getBookNameEn()).isEqualTo("New Name");
        assertThat(updated.getAuthorNameMr()).isEqualTo("नवीन लेखक");
        assertThat(updated.getReadingStatus()).isEqualTo(ReadingStatus.FINISHED);
        assertThat(updated.getRating()).isEqualTo(4);
        assertThat(updated.getLocation()).isEqualTo("Shelf A");
    }

    @Test
    void delete_shouldRemoveBook() throws Exception {
        Book book = new Book();
        book.setBookNameEn("To Delete");
        book.setAuthorNameEn("Author");
        book.setReadingStatus(ReadingStatus.UNREAD);
        book.setGenre("Unknown");
        book.setBookLanguage("Unknown");
        book = bookRepository.save(book);

        mockMvc.perform(post("/books/{id}/delete", book.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    @Test
    void search_shouldMatchAcrossBookAndAuthorFields() throws Exception {
        Book one = new Book();
        one.setBookNameEn("Atomic Habits");
        one.setAuthorNameEn("James Clear");
        one.setBookNameMr("अॅटोमिक हॅबिट्स");
        one.setAuthorNameMr("जेम्स क्लिअर");
        one.setReadingStatus(ReadingStatus.FINISHED);
        one.setGenre("Self-Help");
        one.setBookLanguage("English");
        bookRepository.save(one);

        Book two = new Book();
        two.setBookNameEn("Clean Code");
        two.setAuthorNameEn("Robert Martin");
        two.setReadingStatus(ReadingStatus.UNREAD);
        two.setGenre("Technology");
        two.setBookLanguage("English");
        bookRepository.save(two);

        mockMvc.perform(get("/books").param("query", "जेम्स"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Atomic Habits")))
                .andExpect(content().string(not(containsString("Clean Code"))));
    }

    @Test
    void filterAndSort_shouldApplyCombinedCriteria() throws Exception {
        Book one = new Book();
        one.setBookNameEn("Book One");
        one.setAuthorNameEn("Author A");
        one.setReadingStatus(ReadingStatus.FINISHED);
        one.setRating(5);
        one.setGenre("Fiction");
        one.setBookLanguage("English");
        one.setCustomTags("memoir, dalit");
        bookRepository.save(one);

        Book two = new Book();
        two.setBookNameEn("Book Two");
        two.setAuthorNameEn("Author B");
        two.setReadingStatus(ReadingStatus.FINISHED);
        two.setRating(3);
        two.setGenre("Fiction");
        two.setBookLanguage("English");
        two.setCustomTags("memoir");
        bookRepository.save(two);

        Book noMatch = new Book();
        noMatch.setBookNameEn("Book Three");
        noMatch.setAuthorNameEn("Author C");
        noMatch.setReadingStatus(ReadingStatus.UNREAD);
        noMatch.setRating(5);
        noMatch.setGenre("Mystery");
        noMatch.setBookLanguage("Marathi");
        noMatch.setCustomTags("science");
        bookRepository.save(noMatch);

        mockMvc.perform(get("/books")
                        .param("readingStatus", "FINISHED")
                        .param("minRating", "4")
                        .param("genre", "Fiction")
                        .param("bookLanguage", "English")
                        .param("tags", "dalit")
                        .param("sortBy", "rating")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Book One")))
                .andExpect(content().string(not(containsString("Book Two"))))
                .andExpect(content().string(not(containsString("Book Three"))));
    }

    @Test
    void wishlistCrud_shouldCreateRenameAddAndDeleteItem() throws Exception {
        mockMvc.perform(post("/wishlists").param("name", "Reading Queue"))
                .andExpect(status().is3xxRedirection());

        Wishlist wishlist = wishlistRepository.findByNameIgnoreCase("Reading Queue").orElseThrow();

        mockMvc.perform(post("/wishlists/{id}/rename", wishlist.getId()).param("name", "Top Picks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?wishlistId=" + wishlist.getId()));

        Wishlist renamed = wishlistRepository.findById(wishlist.getId()).orElseThrow();
        assertThat(renamed.getName()).isEqualTo("Top Picks");

        mockMvc.perform(post("/wishlists/{id}/items", wishlist.getId())
                        .param("bookNameEn", "The Pragmatic Programmer")
                        .param("authorNameEn", "Andy Hunt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?wishlistId=" + wishlist.getId()));

        WishlistItem item = wishlistItemRepository.findAllByWishlistIdOrderByIdAsc(wishlist.getId()).getFirst();
        assertThat(item.getBookNameEn()).isEqualTo("The Pragmatic Programmer");

        mockMvc.perform(post("/wishlists/{wishlistId}/items/{itemId}/delete", wishlist.getId(), item.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?wishlistId=" + wishlist.getId()));

        assertThat(wishlistItemRepository.findAllByWishlistIdOrderByIdAsc(wishlist.getId())).isEmpty();
    }

    @Test
    void createWishlist_shouldRejectUnsafeHtmlInput() throws Exception {
        mockMvc.perform(post("/wishlists").param("name", "<b>Queue</b>"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(wishlistRepository.count()).isZero();
    }

    @Test
    void booksPage_shouldIgnoreInvalidSortAndQueryInput() throws Exception {
        mockMvc.perform(get("/books")
                        .param("sortBy", "id;drop table books")
                        .param("query", "<script>alert(1)</script>"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid filter input was ignored.")));
    }
}
