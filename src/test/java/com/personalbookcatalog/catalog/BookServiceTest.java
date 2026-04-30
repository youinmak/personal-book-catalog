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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void create_shouldApplyDefaultsAndNormalizeValues() {
        BookForm form = new BookForm();
        form.setBookNameEn("  Clean Code  ");
        form.setBookNameMr("  स्वच्छ कोड  ");
        form.setAuthorNameEn("  Robert C. Martin ");
        form.setAuthorNameMr("  रॉबर्ट सी. मार्टिन ");

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.create(form);

        assertThat(result.getBookNameEn()).isEqualTo("Clean Code");
        assertThat(result.getAuthorNameEn()).isEqualTo("Robert C. Martin");
        assertThat(result.getReadingStatus()).isEqualTo(ReadingStatus.UNREAD);
        assertThat(result.getBookLanguage()).isEqualTo("Unknown");
        assertThat(result.getLocation()).isEqualTo("Asad");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_shouldThrowWhenIdNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, new BookForm()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_shouldThrowWhenIdNotFound() {
        when(bookRepository.existsById(11L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.delete(11L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("11");
    }

    @Test
    void findAll_shouldFilterAndSortByAuthorNameDescending() {
        Book low = new Book();
        low.setId(1L);
        low.setBookNameEn("Book A");
        low.setAuthorNameEn("Author A");
        low.setReadingStatus(ReadingStatus.FINISHED);
        low.setBookLanguage("English");

        Book high = new Book();
        high.setId(2L);
        high.setBookNameEn("Book B");
        high.setAuthorNameEn("Author B");
        high.setReadingStatus(ReadingStatus.FINISHED);
        high.setBookLanguage("English");

        Book noMatch = new Book();
        noMatch.setId(3L);
        noMatch.setBookNameEn("Book C");
        noMatch.setAuthorNameEn("Author C");
        noMatch.setReadingStatus(ReadingStatus.UNREAD);
        noMatch.setBookLanguage("Marathi");

        when(bookRepository.findAll()).thenReturn(List.of(low, high, noMatch));

        BookListCriteria criteria = new BookListCriteria();
        criteria.setReadingStatus(ReadingStatus.FINISHED);
        criteria.setBookLanguage("English");
        criteria.setSortBy("authorName");
        criteria.setSortDir("desc");

        List<Book> result = bookService.findAll(criteria);

        assertThat(result).extracting(Book::getId).containsExactly(2L, 1L);
    }
}
