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

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void booksPage_shouldLoad() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Personal Book Catalog")));
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
    void update_shouldModifyExistingBook() throws Exception {
        Book book = new Book();
        book.setBookNameEn("Old Name");
        book.setAuthorNameEn("Old Author");
        book = bookRepository.save(book);

        mockMvc.perform(post("/books/{id}/update", book.getId())
                        .param("bookNameEn", "New Name")
                        .param("bookNameMr", "नवीन नाव")
                        .param("authorNameEn", "New Author")
                        .param("authorNameMr", "नवीन लेखक"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(updated.getBookNameEn()).isEqualTo("New Name");
        assertThat(updated.getAuthorNameMr()).isEqualTo("नवीन लेखक");
    }

    @Test
    void delete_shouldRemoveBook() throws Exception {
        Book book = new Book();
        book.setBookNameEn("To Delete");
        book.setAuthorNameEn("Author");
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
        bookRepository.save(one);

        Book two = new Book();
        two.setBookNameEn("Clean Code");
        two.setAuthorNameEn("Robert Martin");
        bookRepository.save(two);

        mockMvc.perform(get("/books").param("query", "जेम्स"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Atomic Habits")))
                .andExpect(content().string(not(containsString("Clean Code"))));
    }
}
