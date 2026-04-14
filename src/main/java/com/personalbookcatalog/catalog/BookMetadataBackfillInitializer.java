package com.personalbookcatalog.catalog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Backfills newly added metadata fields for older rows.
 */
@Component
public class BookMetadataBackfillInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(BookMetadataBackfillInitializer.class);

    private final BookRepository bookRepository;

    /**
     * Creates backfill initializer with repository dependency.
     */
    public BookMetadataBackfillInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Ensures existing rows always have safe defaults for new metadata fields.
     */
    @Override
    public void run(ApplicationArguments args) {
        List<Book> books = bookRepository.findAll();
        int changed = 0;
        for (Book book : books) {
            boolean updated = false;
            if (book.getReadingStatus() == null) {
                book.setReadingStatus(ReadingStatus.UNREAD);
                updated = true;
            }
            if (!hasText(book.getGenre())) {
                book.setGenre("Unknown");
                updated = true;
            }
            if (!hasText(book.getBookLanguage())) {
                book.setBookLanguage("Unknown");
                updated = true;
            }
            if (updated) {
                changed++;
            }
        }
        if (changed > 0) {
            bookRepository.saveAll(books);
            logger.info("Backfilled metadata defaults for {} existing books.", changed);
        }
    }

    /**
     * Returns true when value contains non-blank text.
     */
    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
