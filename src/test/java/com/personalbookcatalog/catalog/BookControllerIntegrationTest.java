package com.personalbookcatalog.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockHttpSession;
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
                .andExpect(content().string(containsString("Read-Only Mode")));
    }

    @Test
    void booksPage_shouldRenderMarathiWhenLangMrSelected() throws Exception {
        mockMvc.perform(get("/books").param("lang", "mr"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("वैयक्तिक पुस्तक सूची")))
                .andExpect(content().string(containsString("फक्त पाहण्याचा मोड")));
    }

    @Test
    void booksPage_shouldPersistMarathiLocaleInSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/books").param("lang", "mr").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("वैयक्तिक पुस्तक सूची")));

        mockMvc.perform(get("/books").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("वैयक्तिक पुस्तक सूची")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldPersistBookAndRedirect() throws Exception {
        mockMvc.perform(post("/books")
                        .with(csrf())
                        .param("bookNameEn", "Atomic Habits")
                        .param("bookNameMr", "अॅटोमिक हॅबिट्स")
                        .param("authorNameEn", "James Clear")
                        .param("authorNameMr", "जेम्स क्लिअर"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldRejectWhenEnglishFieldsMissing() throws Exception {
        mockMvc.perform(post("/books")
                        .with(csrf())
                        .param("bookNameEn", "")
                        .param("authorNameEn", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldRejectUnsafeHtmlInput() throws Exception {
        mockMvc.perform(post("/books")
                        .with(csrf())
                        .param("bookNameEn", "<script>alert(1)</script>")
                        .param("authorNameEn", "Safe Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        assertThat(bookRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldModifyExistingBook() throws Exception {
        Book book = new Book();
        book.setBookNameEn("Old Name");
        book.setAuthorNameEn("Old Author");
        book.setReadingStatus(ReadingStatus.UNREAD);
        book.setBookLanguage("Unknown");
        book = bookRepository.save(book);

        mockMvc.perform(post("/books/{id}/update", book.getId())
                        .with(csrf())
                        .param("bookNameEn", "New Name")
                        .param("bookNameMr", "नवीन नाव")
                        .param("authorNameEn", "New Author")
                        .param("authorNameMr", "नवीन लेखक")
                        .param("readingStatus", "FINISHED")
                        .param("bookLanguage", "English")
                        .param("location", "Shelf A"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(updated.getBookNameEn()).isEqualTo("New Name");
        assertThat(updated.getAuthorNameMr()).isEqualTo("नवीन लेखक");
        assertThat(updated.getReadingStatus()).isEqualTo(ReadingStatus.FINISHED);
        assertThat(updated.getLocation()).isEqualTo("Shelf A");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldRemoveBook() throws Exception {
        Book book = new Book();
        book.setBookNameEn("To Delete");
        book.setAuthorNameEn("Author");
        book.setReadingStatus(ReadingStatus.UNREAD);
        book.setBookLanguage("Unknown");
        book = bookRepository.save(book);

        mockMvc.perform(post("/books/{id}/delete", book.getId()).with(csrf()))
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
        one.setBookLanguage("English");
        bookRepository.save(one);

        Book two = new Book();
        two.setBookNameEn("Clean Code");
        two.setAuthorNameEn("Robert Martin");
        two.setReadingStatus(ReadingStatus.UNREAD);
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
        one.setAuthorNameEn("Author B");
        one.setReadingStatus(ReadingStatus.FINISHED);
        one.setBookLanguage("English");
        bookRepository.save(one);

        Book two = new Book();
        two.setBookNameEn("Book Two");
        two.setAuthorNameEn("Author A");
        two.setReadingStatus(ReadingStatus.FINISHED);
        two.setBookLanguage("English");
        bookRepository.save(two);

        Book noMatch = new Book();
        noMatch.setBookNameEn("Book Three");
        noMatch.setAuthorNameEn("Author C");
        noMatch.setReadingStatus(ReadingStatus.UNREAD);
        noMatch.setBookLanguage("Marathi");
        bookRepository.save(noMatch);

        mockMvc.perform(get("/books")
                        .param("readingStatus", "FINISHED")
                        .param("bookLanguage", "English")
                        .param("sortBy", "authorName")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Book One")))
                .andExpect(content().string(not(containsString("Book Three"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void wishlistCrud_shouldCreateRenameAddAndDeleteItem() throws Exception {
        mockMvc.perform(post("/wishlists").with(csrf()).param("name", "Reading Queue"))
                .andExpect(status().is3xxRedirection());

        Wishlist wishlist = wishlistRepository.findByNameIgnoreCase("Reading Queue").orElseThrow();

        mockMvc.perform(post("/wishlists/{id}/rename", wishlist.getId()).with(csrf()).param("name", "Top Picks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?wishlistId=" + wishlist.getId()));

        Wishlist renamed = wishlistRepository.findById(wishlist.getId()).orElseThrow();
        assertThat(renamed.getName()).isEqualTo("Top Picks");

        mockMvc.perform(post("/wishlists/{id}/items", wishlist.getId())
                        .with(csrf())
                        .param("bookNameEn", "The Pragmatic Programmer")
                        .param("authorNameEn", "Andy Hunt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?wishlistId=" + wishlist.getId()));

        WishlistItem item = wishlistItemRepository.findAllByWishlistIdOrderByIdAsc(wishlist.getId()).getFirst();
        assertThat(item.getBookNameEn()).isEqualTo("The Pragmatic Programmer");

        mockMvc.perform(post("/wishlists/{wishlistId}/items/{itemId}/delete", wishlist.getId(), item.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?wishlistId=" + wishlist.getId()));

        assertThat(wishlistItemRepository.findAllByWishlistIdOrderByIdAsc(wishlist.getId())).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createWishlist_shouldRejectUnsafeHtmlInput() throws Exception {
        mockMvc.perform(post("/wishlists").with(csrf()).param("name", "<b>Queue</b>"))
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

    @Test
    void anonymousPost_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/books")
                        .param("bookNameEn", "Restricted")
                        .param("authorNameEn", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?denied=true"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPostWithoutCsrf_shouldBeForbidden() throws Exception {
        mockMvc.perform(post("/books")
                        .param("bookNameEn", "No Csrf")
                        .param("authorNameEn", "Admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?denied=true"));
    }
}
