package com.personalbookcatalog.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String bookNameEn;

    @Column(length = 300)
    private String bookNameMr;

    @Column(nullable = false, length = 300)
    private String authorNameEn;

    @Column(length = 300)
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
