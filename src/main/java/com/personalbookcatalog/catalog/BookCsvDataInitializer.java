package com.personalbookcatalog.catalog;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BookCsvDataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(BookCsvDataInitializer.class);

    private final BookRepository bookRepository;
    private final String csvPath;
    private final boolean bootstrapEnabled;
    private final String markerPath;

    public BookCsvDataInitializer(BookRepository bookRepository,
                                  @Value("${app.bootstrap.book-csv-path:./data/BookList.csv}") String csvPath,
                                  @Value("${app.bootstrap.enabled:true}") boolean bootstrapEnabled,
                                  @Value("${app.bootstrap.marker-path:./data/.book-bootstrap.done}") String markerPath) {
        this.bookRepository = bookRepository;
        this.csvPath = csvPath;
        this.bootstrapEnabled = bootstrapEnabled;
        this.markerPath = markerPath;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!bootstrapEnabled) {
            logger.info("Book CSV bootstrap is disabled.");
            return;
        }

        Path doneMarker = Paths.get(markerPath);
        if (Files.exists(doneMarker)) {
            logger.info("Book CSV bootstrap marker found, skipping import.");
            return;
        }

        if (bookRepository.count() > 0) {
            logger.info("Book table already has data, skipping CSV bootstrap import.");
            writeMarker(doneMarker);
            return;
        }

        Path filePath = Paths.get(csvPath);
        if (!Files.exists(filePath)) {
            logger.warn("Book bootstrap CSV not found at path: {}", filePath.toAbsolutePath());
            return;
        }

        List<Book> books = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setIgnoreEmptyLines(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {
            boolean firstRecord = true;
            for (CSVRecord record : parser) {
                if (record.size() < 2) {
                    continue;
                }
                String bookNameEn = normalize(record.get(0));
                String authorNameEn = normalize(record.get(1));
                if (firstRecord && "Book Title".equalsIgnoreCase(bookNameEn)
                        && "Book Author".equalsIgnoreCase(authorNameEn)) {
                    firstRecord = false;
                    continue;
                }
                firstRecord = false;
                if (!StringUtils.hasText(bookNameEn) || !StringUtils.hasText(authorNameEn)) {
                    continue;
                }
                Book book = new Book();
                book.setBookNameEn(bookNameEn);
                book.setAuthorNameEn(authorNameEn);
                books.add(book);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to import books from CSV file: " + filePath, exception);
        }

        if (books.isEmpty()) {
            logger.info("No valid book records found in CSV file: {}", filePath.toAbsolutePath());
            return;
        }

        bookRepository.saveAll(books);
        writeMarker(doneMarker);
        logger.info("Imported {} books from CSV file: {}", books.size(), filePath.toAbsolutePath());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void writeMarker(Path doneMarker) {
        try {
            Path parent = doneMarker.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(doneMarker, "bootstrap-complete-at=" + Instant.now(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write bootstrap marker: " + doneMarker, exception);
        }
    }
}
