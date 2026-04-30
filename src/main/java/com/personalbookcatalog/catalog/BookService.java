package com.personalbookcatalog.catalog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Handles catalog business logic, including CRUD and list filtering/sorting.
 */
@Service
public class BookService {

    private static final String DEFAULT_LOCATION = "Asad";

    private static final List<String> DEFAULT_LANGUAGES = List.of(
            "Unknown", "English", "Marathi", "Hindi", "Sanskrit", "Tamil");

    private final BookRepository bookRepository;

    /**
     * Creates service with repository dependency.
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Returns all books ordered by recency.
     */
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>(bookRepository.findAll());
        books.sort(Comparator.comparing(Book::getId, Comparator.nullsLast(Comparator.reverseOrder())));
        return books;
    }

    /**
     * Returns books matching combined list criteria.
     */
    @Transactional(readOnly = true)
    public List<Book> findAll(BookListCriteria criteria) {
        List<Book> books = new ArrayList<>(bookRepository.findAll());
        return books.stream()
                .filter(book -> matchesQuery(book, criteria.getQuery()))
                .filter(book -> matchesReadingStatus(book, criteria.getReadingStatus()))
                .filter(book -> matchesExactText(book.getBookLanguage(), criteria.getBookLanguage()))
                .sorted(buildComparator(criteria.getSortBy(), criteria.getSortDir()))
                .toList();
    }

    /**
     * Creates a new book from form data.
     */
    @Transactional
    public Book create(BookForm form) {
        Book book = new Book();
        updateEntity(book, form);
        return bookRepository.save(book);
    }

    /**
     * Updates an existing book by id.
     */
    @Transactional
    public Book update(Long id, BookForm form) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found for id: " + id));
        updateEntity(existingBook, form);
        return bookRepository.save(existingBook);
    }

    /**
     * Deletes a book by id.
     */
    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NoSuchElementException("Book not found for id: " + id);
        }
        bookRepository.deleteById(id);
    }

    /**
     * Returns editable book language suggestion values for datalist input.
     */
    @Transactional(readOnly = true)
    public List<String> getLanguageSuggestions() {
        LinkedHashSet<String> values = new LinkedHashSet<>(DEFAULT_LANGUAGES);
        bookRepository.findAll().stream()
                .map(Book::getBookLanguage)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .forEach(values::add);
        return new ArrayList<>(values);
    }

    /**
     * Applies form data to the target entity with defaults and normalization.
     */
    private void updateEntity(Book book, BookForm form) {
        book.setBookNameEn(trimToNull(form.getBookNameEn()));
        book.setBookNameMr(trimToNull(form.getBookNameMr()));
        book.setAuthorNameEn(trimToNull(form.getAuthorNameEn()));
        book.setAuthorNameMr(trimToNull(form.getAuthorNameMr()));
        book.setReadingStatus(form.getReadingStatus() == null ? ReadingStatus.UNREAD : form.getReadingStatus());
        book.setBookLanguage(defaultIfEmpty(form.getBookLanguage(), "Unknown"));
        book.setLocation(defaultIfEmpty(form.getLocation(), DEFAULT_LOCATION));
    }

    /**
     * Returns true when query matches any searchable book field.
     */
    private boolean matchesQuery(Book book, String query) {
        if (!StringUtils.hasText(query)) {
            return true;
        }
        String q = query.trim().toLowerCase();
        return containsIgnoreCase(book.getBookNameEn(), q)
                || containsIgnoreCase(book.getBookNameMr(), q)
                || containsIgnoreCase(book.getAuthorNameEn(), q)
                || containsIgnoreCase(book.getAuthorNameMr(), q)
                || containsIgnoreCase(book.getBookLanguage(), q)
                || containsIgnoreCase(book.getLocation(), q)
                || containsIgnoreCase(book.getReadingStatus() == null ? null : book.getReadingStatus().name(), q);
    }

    /**
     * Returns true when reading status matches or filter is absent.
     */
    private boolean matchesReadingStatus(Book book, ReadingStatus status) {
        if (status == null) {
            return true;
        }
        ReadingStatus current = book.getReadingStatus() == null ? ReadingStatus.UNREAD : book.getReadingStatus();
        return current == status;
    }

    /**
     * Returns true for exact, case-insensitive string match or absent filter.
     */
    private boolean matchesExactText(String value, String filterValue) {
        if (!StringUtils.hasText(filterValue)) {
            return true;
        }
        return StringUtils.hasText(value) && value.trim().equalsIgnoreCase(filterValue.trim());
    }

    /**
     * Builds comparator for requested sort field and direction.
     */
    private Comparator<Book> buildComparator(String sortBy, String sortDir) {
        boolean asc = "asc".equalsIgnoreCase(sortDir);
        String normalizedSortBy = StringUtils.hasText(sortBy) ? sortBy.trim().toLowerCase() : "id";

        return switch (normalizedSortBy) {
            case "bookname" -> applyDirection(
                    Comparator.comparing(book -> nullSafeLower(book.getBookNameEn()), String::compareTo),
                    asc);
            case "authorname" -> applyDirection(
                    Comparator.comparing(book -> nullSafeLower(book.getAuthorNameEn()), String::compareTo),
                    asc);
            case "readingstatus" -> applyDirection(
                    Comparator.comparing(book -> book.getReadingStatus() == null ? ReadingStatus.UNREAD : book.getReadingStatus()),
                    asc);
            default -> asc
                    ? Comparator.comparing(Book::getId, Comparator.nullsLast(Long::compareTo))
                    : Comparator.comparing(Book::getId, Comparator.nullsLast(Comparator.reverseOrder()));
        };
    }

    /**
     * Applies direction for comparators where direct reversal is safe.
     */
    private Comparator<Book> applyDirection(Comparator<Book> comparator, boolean asc) {
        return asc ? comparator : comparator.reversed();
    }

    /**
     * Returns lower-case representation for case-insensitive operations.
     */
    private String nullSafeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    /**
     * Returns true if text contains lower-case needle.
     */
    private boolean containsIgnoreCase(String value, String lowerNeedle) {
        return value != null && value.toLowerCase().contains(lowerNeedle);
    }

    /**
     * Trims text and converts blank values to null.
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Returns default value when input is empty.
     */
    private String defaultIfEmpty(String value, String defaultValue) {
        String trimmed = trimToNull(value);
        return trimmed == null ? defaultValue : trimmed;
    }
}
