package com.personalbookcatalog.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Sort;
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
    void create_shouldTrimAndPersistValues() {
        BookForm form = new BookForm();
        form.setBookNameEn("  Clean Code  ");
        form.setBookNameMr("  स्वच्छ कोड  ");
        form.setAuthorNameEn("  Robert C. Martin ");
        form.setAuthorNameMr("  रॉबर्ट सी. मार्टिन ");

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.create(form);

        assertThat(result.getBookNameEn()).isEqualTo("Clean Code");
        assertThat(result.getBookNameMr()).isEqualTo("स्वच्छ कोड");
        assertThat(result.getAuthorNameEn()).isEqualTo("Robert C. Martin");
        assertThat(result.getAuthorNameMr()).isEqualTo("रॉबर्ट सी. मार्टिन");
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
    void findAll_shouldSearchAcrossAllFieldsWhenQueryProvided() {
        when(bookRepository.searchAcrossAllFields(anyString(), any(Sort.class))).thenReturn(List.of(new Book()));

        List<Book> result = bookService.findAll("  james ");

        assertThat(result).hasSize(1);
        verify(bookRepository).searchAcrossAllFields("james", Sort.by(Sort.Direction.DESC, "id"));
    }
}
