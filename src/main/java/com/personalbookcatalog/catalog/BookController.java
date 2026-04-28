package com.personalbookcatalog.catalog;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Locale;

import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC controller for catalog and wishlist operations on the same page.
 */
@Controller
@RequestMapping
public class BookController {

    private static final int BOOKS_PER_PAGE = 10;

    private final BookService bookService;
    private final WishlistService wishlistService;
    private final MessageSource messageSource;

    /**
     * Creates controller with service dependencies.
     */
    public BookController(BookService bookService, WishlistService wishlistService, MessageSource messageSource) {
        this.bookService = bookService;
        this.wishlistService = wishlistService;
        this.messageSource = messageSource;
    }

    /**
     * Renders books page with combined search/filter/sort and wishlist panel.
     */
    @GetMapping({"/", "/books"})
    public String getBooksPage(
            @Valid @ModelAttribute("criteria") BookListCriteria criteria,
            BindingResult criteriaBindingResult,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "wishlistId", required = false) Long wishlistId,
            @RequestParam(name = "denied", defaultValue = "false") boolean denied,
            Locale locale,
            Model model) {
        if (criteriaBindingResult.hasErrors()) {
            model.addAttribute("errorMessage", message("message.filter.invalid", locale));
            criteria = new BookListCriteria();
            model.addAttribute("criteria", criteria);
        }
        if (denied) {
            model.addAttribute("errorMessage", message("message.auth.denied", locale));
        }
        List<Book> filteredBooks = bookService.findAll(criteria);
        int totalResults = filteredBooks.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / BOOKS_PER_PAGE));
        int currentPage = Math.max(1, Math.min(page, totalPages));
        int fromIndex = (currentPage - 1) * BOOKS_PER_PAGE;
        int toIndex = Math.min(fromIndex + BOOKS_PER_PAGE, totalResults);
        List<Book> pageBooks = totalResults == 0 ? List.of() : filteredBooks.subList(fromIndex, toIndex);

        model.addAttribute("books", pageBooks);
        model.addAttribute("totalResults", totalResults);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("readingStatuses", List.of(ReadingStatus.values()));
        model.addAttribute("genreSuggestions", bookService.getGenreSuggestions());
        model.addAttribute("languageSuggestions", bookService.getLanguageSuggestions());

        if (!model.containsAttribute("newBookForm")) {
            BookForm bookForm = new BookForm();
            bookForm.setReadingStatus(ReadingStatus.UNREAD);
            bookForm.setLocation("Asad");
            model.addAttribute("newBookForm", bookForm);
        }

        populateWishlistModel(model, wishlistId);
        return "books";
    }

    /**
     * Creates a new book and redirects back to list page.
     */
    @PostMapping("/books")
    public String createBook(
            @Valid @ModelAttribute("newBookForm") BookForm form,
            BindingResult bindingResult,
            Locale locale,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstValidationMessage(bindingResult));
            redirectAttributes.addFlashAttribute("newBookForm", form);
            return "redirect:/books";
        }
        try {
            bookService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", message("message.book.add.success", locale));
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
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute BookForm form,
            BindingResult bindingResult,
            Locale locale,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstValidationMessage(bindingResult));
            return "redirect:/books";
        }
        try {
            bookService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", message("message.book.update.success", locale));
        } catch (NoSuchElementException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Deletes a book and redirects back to list page.
     */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, Locale locale, RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", message("message.book.delete.success", locale));
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Creates a new wishlist and makes it active.
     */
    @PostMapping("/wishlists")
    public String createWishlist(
            @Valid @ModelAttribute WishlistForm form,
            BindingResult bindingResult,
            Locale locale,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstValidationMessage(bindingResult));
            return "redirect:/books";
        }
        try {
            Wishlist wishlist = wishlistService.createWishlist(form.getName());
            redirectAttributes.addFlashAttribute("successMessage", message("message.wishlist.create.success", locale));
            return redirectToBooks(wishlist.getId());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/books";
        }
    }

    /**
     * Renames an existing wishlist.
     */
    @PostMapping("/wishlists/{id}/rename")
    public String renameWishlist(
            @PathVariable Long id,
            @Valid @ModelAttribute WishlistRenameForm form,
            BindingResult bindingResult,
            Locale locale,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstValidationMessage(bindingResult));
            return redirectToBooks(id);
        }
        try {
            wishlistService.renameWishlist(id, form.getName());
            redirectAttributes.addFlashAttribute("successMessage", message("message.wishlist.rename.success", locale));
        } catch (NoSuchElementException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return redirectToBooks(id);
    }

    /**
     * Deletes wishlist and redirects to next active wishlist if available.
     */
    @PostMapping("/wishlists/{id}/delete")
    public String deleteWishlist(@PathVariable Long id, Locale locale, RedirectAttributes redirectAttributes) {
        try {
            Long nextActiveId = wishlistService.deleteWishlist(id);
            redirectAttributes.addFlashAttribute("successMessage", message("message.wishlist.delete.success", locale));
            return redirectToBooks(nextActiveId);
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/books";
        }
    }

    /**
     * Adds a new item to the selected wishlist.
     */
    @PostMapping("/wishlists/{id}/items")
    public String addWishlistItem(
            @PathVariable Long id,
            @Valid @ModelAttribute WishlistItemForm form,
            BindingResult bindingResult,
            Locale locale,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstValidationMessage(bindingResult));
            return redirectToBooks(id);
        }
        try {
            wishlistService.addItem(id, form.getBookNameEn(), form.getAuthorNameEn());
            redirectAttributes.addFlashAttribute("successMessage", message("message.wishlist.item.add.success", locale));
        } catch (NoSuchElementException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return redirectToBooks(id);
    }

    /**
     * Deletes an item from the selected wishlist.
     */
    @PostMapping("/wishlists/{wishlistId}/items/{itemId}/delete")
    public String deleteWishlistItem(
            @PathVariable Long wishlistId,
            @PathVariable Long itemId,
            Locale locale,
            RedirectAttributes redirectAttributes) {
        try {
            wishlistService.deleteItem(wishlistId, itemId);
            redirectAttributes.addFlashAttribute("successMessage", message("message.wishlist.item.delete.success", locale));
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return redirectToBooks(wishlistId);
    }

    /**
     * Fills model with wishlist panel state and form objects.
     */
    private void populateWishlistModel(Model model, Long requestedWishlistId) {
        List<Wishlist> wishlists = wishlistService.findAllWishlists();
        Wishlist activeWishlist = wishlistService.getActiveWishlist(requestedWishlistId);

        model.addAttribute("wishlists", wishlists);
        model.addAttribute("activeWishlist", activeWishlist);
        model.addAttribute("activeWishlistId", activeWishlist == null ? null : activeWishlist.getId());

        if (!model.containsAttribute("newWishlistForm")) {
            model.addAttribute("newWishlistForm", new WishlistForm());
        }
        if (!model.containsAttribute("renameWishlistForm")) {
            WishlistRenameForm renameForm = new WishlistRenameForm();
            if (activeWishlist != null) {
                renameForm.setName(activeWishlist.getName());
            }
            model.addAttribute("renameWishlistForm", renameForm);
        }
        if (!model.containsAttribute("newWishlistItemForm")) {
            model.addAttribute("newWishlistItemForm", new WishlistItemForm());
        }
    }

    /**
     * Builds redirect URL to books page with optional active wishlist id.
     */
    private String redirectToBooks(Long wishlistId) {
        if (wishlistId == null) {
            return "redirect:/books";
        }
        return "redirect:/books?wishlistId=" + wishlistId;
    }

    /**
     * Returns first field or global validation error message.
     */
    private String firstValidationMessage(BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return bindingResult.getFieldErrors().getFirst().getDefaultMessage();
        }
        if (bindingResult.hasGlobalErrors()) {
            return bindingResult.getGlobalErrors().getFirst().getDefaultMessage();
        }
        return "Invalid input.";
    }

    private String message(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
