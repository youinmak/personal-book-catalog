package com.personalbookcatalog.catalog;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(String query) {
        if (!StringUtils.hasText(query)) {
            return findAll();
        }
        return bookRepository.searchAcrossAllFields(query.trim(), Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional
    public Book create(BookForm form) {
        Book book = new Book();
        updateEntity(book, form);
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, BookForm form) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found for id: " + id));
        updateEntity(existingBook, form);
        return bookRepository.save(existingBook);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NoSuchElementException("Book not found for id: " + id);
        }
        bookRepository.deleteById(id);
    }

    private void updateEntity(Book book, BookForm form) {
        book.setBookNameEn(trimToNull(form.getBookNameEn()));
        book.setBookNameMr(trimToNull(form.getBookNameMr()));
        book.setAuthorNameEn(trimToNull(form.getAuthorNameEn()));
        book.setAuthorNameMr(trimToNull(form.getAuthorNameMr()));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
