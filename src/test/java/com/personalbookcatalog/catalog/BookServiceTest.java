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
        form.setCustomTags("Memoir, memoir, Dalit literature");

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.create(form);

        assertThat(result.getBookNameEn()).isEqualTo("Clean Code");
        assertThat(result.getAuthorNameEn()).isEqualTo("Robert C. Martin");
        assertThat(result.getReadingStatus()).isEqualTo(ReadingStatus.UNREAD);
        assertThat(result.getGenre()).isEqualTo("Unknown");
        assertThat(result.getBookLanguage()).isEqualTo("Unknown");
        assertThat(result.getLocation()).isEqualTo("Asad");
        assertThat(result.getCustomTags()).isEqualTo("memoir, dalit literature");
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
    void findAll_shouldFilterAndSortByRatingDescending() {
        Book low = new Book();
        low.setId(1L);
        low.setBookNameEn("A");
        low.setAuthorNameEn("X");
        low.setReadingStatus(ReadingStatus.FINISHED);
        low.setRating(2);
        low.setGenre("Fiction");
        low.setBookLanguage("English");
        low.setCustomTags("memoir");

        Book high = new Book();
        high.setId(2L);
        high.setBookNameEn("B");
        high.setAuthorNameEn("Y");
        high.setReadingStatus(ReadingStatus.FINISHED);
        high.setRating(5);
        high.setGenre("Fiction");
        high.setBookLanguage("English");
        high.setCustomTags("memoir, dalit");

        Book noMatch = new Book();
        noMatch.setId(3L);
        noMatch.setBookNameEn("C");
        noMatch.setAuthorNameEn("Z");
        noMatch.setReadingStatus(ReadingStatus.UNREAD);
        noMatch.setRating(5);
        noMatch.setGenre("Mystery");
        noMatch.setBookLanguage("Marathi");
        noMatch.setCustomTags("science");

        when(bookRepository.findAll()).thenReturn(List.of(low, high, noMatch));

        BookListCriteria criteria = new BookListCriteria();
        criteria.setReadingStatus(ReadingStatus.FINISHED);
        criteria.setMinRating(2);
        criteria.setGenre("Fiction");
        criteria.setBookLanguage("English");
        criteria.setTags("dalit,classic");
        criteria.setSortBy("rating");
        criteria.setSortDir("desc");

        List<Book> result = bookService.findAll(criteria);

        assertThat(result).extracting(Book::getId).containsExactly(2L);
    }
}
