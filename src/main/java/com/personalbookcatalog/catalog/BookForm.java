package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookForm {

    private Long id;

    @NotBlank(message = "Book name (English) is required.")
    @Size(max = 300, message = "Book name (English) must be at most 300 characters.")
    private String bookNameEn;

    @Size(max = 300, message = "Book name (Marathi) must be at most 300 characters.")
    private String bookNameMr;

    @NotBlank(message = "Author name (English) is required.")
    @Size(max = 300, message = "Author name (English) must be at most 300 characters.")
    private String authorNameEn;

    @Size(max = 300, message = "Author name (Marathi) must be at most 300 characters.")
    private String authorNameMr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookNameEn() {
        return bookNameEn;
    }

    public void setBookNameEn(String bookNameEn) {
        this.bookNameEn = bookNameEn;
    }

    public String getBookNameMr() {
        return bookNameMr;
    }

    public void setBookNameMr(String bookNameMr) {
        this.bookNameMr = bookNameMr;
    }

    public String getAuthorNameEn() {
        return authorNameEn;
    }

    public void setAuthorNameEn(String authorNameEn) {
        this.authorNameEn = authorNameEn;
    }

    public String getAuthorNameMr() {
        return authorNameMr;
    }

    public void setAuthorNameMr(String authorNameMr) {
        this.authorNameMr = authorNameMr;
    }
}
