package com.personalbookcatalog.catalog;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping({"/", "/books"})
    public String getBooksPage(@RequestParam(name = "query", required = false) String query, Model model) {
        model.addAttribute("books", bookService.findAll(query));
        model.addAttribute("searchQuery", query == null ? "" : query);
        if (!model.containsAttribute("newBookForm")) {
            model.addAttribute("newBookForm", new BookForm());
        }
        return "books";
    }

    @PostMapping("/books")
    public String createBook(@ModelAttribute("newBookForm") BookForm form, RedirectAttributes redirectAttributes) {
        if (!isValidEnglishFields(form)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Book name (English) and Author name (English) are required.");
            redirectAttributes.addFlashAttribute("newBookForm", form);
            return "redirect:/books";
        }
        bookService.create(form);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully.");
        return "redirect:/books";
    }

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
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/books";
    }

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

    private boolean isValidEnglishFields(BookForm form) {
        return StringUtils.hasText(form.getBookNameEn()) && StringUtils.hasText(form.getAuthorNameEn());
    }
}
