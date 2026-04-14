package com.personalbookcatalog.catalog;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC controller for catalog list, create, update, and delete operations.
 */
@Controller
@RequestMapping
public class BookController {

    private final BookService bookService;

    /**
     * Creates controller with service dependency.
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Renders books page with combined search/filter/sort criteria.
     */
    @GetMapping({"/", "/books"})
    public String getBooksPage(@ModelAttribute("criteria") BookListCriteria criteria, Model model) {
        model.addAttribute("books", bookService.findAll(criteria));
        model.addAttribute("readingStatuses", List.of(ReadingStatus.values()));
        model.addAttribute("genreSuggestions", bookService.getGenreSuggestions());
        model.addAttribute("languageSuggestions", bookService.getLanguageSuggestions());

        if (!model.containsAttribute("newBookForm")) {
            BookForm bookForm = new BookForm();
            bookForm.setReadingStatus(ReadingStatus.UNREAD);
            model.addAttribute("newBookForm", bookForm);
        }
        return "books";
    }

    /**
     * Creates a new book and redirects back to list page.
     */
    @PostMapping("/books")
    public String createBook(@ModelAttribute("newBookForm") BookForm form, RedirectAttributes redirectAttributes) {
        if (!isValidEnglishFields(form)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Book name (English) and Author name (English) are required.");
            redirectAttributes.addFlashAttribute("newBookForm", form);
            return "redirect:/books";
        }
        try {
            bookService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("newBookForm", form);
        }
        return "redirect:/books";
    }

    /**
     * Updates a book and redirects back to list page.
     */
    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable Long id, @ModelAttribute BookForm form, RedirectAttributes redirectAttributes) {
        if (!isValidEnglishFields(form)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Book name (English) and Author name (English) are required for updates.");
            return "redirect:/books";
        }
        try {
            bookService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully.");
        } catch (NoSuchElementException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Deletes a book and redirects back to list page.
     */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Verifies that required English fields are present.
     */
    private boolean isValidEnglishFields(BookForm form) {
        return StringUtils.hasText(form.getBookNameEn()) && StringUtils.hasText(form.getAuthorNameEn());
    }
}
