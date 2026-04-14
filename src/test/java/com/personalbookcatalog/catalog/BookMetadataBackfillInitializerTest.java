package com.personalbookcatalog.catalog;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

class BookMetadataBackfillInitializerTest {

    @Test
    void run_shouldBackfillMissingMetadataDefaults() throws Exception {
        BookRepository repository = org.mockito.Mockito.mock(BookRepository.class);

        Book missing = new Book();
        missing.setBookNameEn("A");
        missing.setAuthorNameEn("B");

        when(repository.findAll()).thenReturn(List.of(missing));
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BookMetadataBackfillInitializer initializer = new BookMetadataBackfillInitializer(repository);
        initializer.run(null);

        verify(repository).saveAll(anyList());
    }
}
