package com.personalbookcatalog.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

class BookCsvDataInitializerTest {

    @TempDir
    Path tempDir;

    @Test
    void run_shouldImportFromCsvWhenDatabaseIsEmpty() throws Exception {
        BookRepository repository = org.mockito.Mockito.mock(BookRepository.class);
        when(repository.count()).thenReturn(0L);
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        Path csv = tempDir.resolve("BookList.csv");
        Path marker = tempDir.resolve(".book-bootstrap.done");
        Files.writeString(csv, """
                Book Title,Book Author,,
                Uchalya,Lakshman Gaikwad,,
                Prak- Cinema,Arun Khopkar,,
                """);

        BookCsvDataInitializer initializer =
                new BookCsvDataInitializer(repository, csv.toString(), true, marker.toString());
        initializer.run(null);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Book>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<Book> imported = captor.getValue();
        assertThat(imported).hasSize(2);
        assertThat(imported.getFirst().getBookNameEn()).isEqualTo("Uchalya");
        assertThat(imported.getFirst().getAuthorNameEn()).isEqualTo("Lakshman Gaikwad");
        assertThat(imported.getFirst().getReadingStatus()).isEqualTo(ReadingStatus.UNREAD);
        assertThat(imported.getFirst().getGenre()).isEqualTo("Unknown");
        assertThat(imported.getFirst().getBookLanguage()).isEqualTo("Unknown");
        assertThat(Files.exists(marker)).isTrue();
    }

    @Test
    void run_shouldSkipImportWhenDataAlreadyExists() throws Exception {
        BookRepository repository = org.mockito.Mockito.mock(BookRepository.class);
        when(repository.count()).thenReturn(3L);

        Path csv = tempDir.resolve("BookList.csv");
        Path marker = tempDir.resolve(".book-bootstrap.done");
        Files.writeString(csv, """
                Book Title,Book Author,,
                A,B,,
                """);

        BookCsvDataInitializer initializer =
                new BookCsvDataInitializer(repository, csv.toString(), true, marker.toString());
        initializer.run(null);

        verify(repository, never()).saveAll(anyList());
        assertThat(Files.exists(marker)).isTrue();
    }

    @Test
    void run_shouldSkipWhenMarkerAlreadyExists() throws Exception {
        BookRepository repository = org.mockito.Mockito.mock(BookRepository.class);
        when(repository.count()).thenReturn(0L);

        Path csv = tempDir.resolve("BookList.csv");
        Path marker = tempDir.resolve(".book-bootstrap.done");
        Files.writeString(csv, """
                Book Title,Book Author,,
                A,B,,
                """);
        Files.writeString(marker, "done");

        BookCsvDataInitializer initializer =
                new BookCsvDataInitializer(repository, csv.toString(), true, marker.toString());
        initializer.run(null);

        verify(repository, never()).saveAll(anyList());
    }
}
