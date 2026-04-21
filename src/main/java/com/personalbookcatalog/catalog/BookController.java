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

    /**
     * Creates controller with service dependencies.
     */
    public BookController(BookService bookService, WishlistService wishlistService) {
        this.bookService = bookService;
        this.wishlistService = wishlistService;
    }

    /**
     * Renders books page with combined search/filter/sort and wishlist panel.
     */
    @GetMapping({"/", "/books"})
    public String getBooksPage(
            @ModelAttribute("criteria") BookListCriteria criteria,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "wishlistId", required = false) Long wishlistId,
            Model model) {
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
     * Creates a new wishlist and makes it active.
     */
    @PostMapping("/wishlists")
    public String createWishlist(@ModelAttribute WishlistForm form, RedirectAttributes redirectAttributes) {
        try {
            Wishlist wishlist = wishlistService.createWishlist(form.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Wishlist created successfully.");
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
            @ModelAttribute WishlistRenameForm form,
            RedirectAttributes redirectAttributes) {
        try {
            wishlistService.renameWishlist(id, form.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Wishlist renamed successfully.");
        } catch (NoSuchElementException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return redirectToBooks(id);
    }

    /**
     * Deletes wishlist and redirects to next active wishlist if available.
     */
    @PostMapping("/wishlists/{id}/delete")
    public String deleteWishlist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Long nextActiveId = wishlistService.deleteWishlist(id);
            redirectAttributes.addFlashAttribute("successMessage", "Wishlist deleted successfully.");
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
            @ModelAttribute WishlistItemForm form,
            RedirectAttributes redirectAttributes) {
        try {
            wishlistService.addItem(id, form.getBookNameEn(), form.getAuthorNameEn());
            redirectAttributes.addFlashAttribute("successMessage", "Wishlist item added successfully.");
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
            RedirectAttributes redirectAttributes) {
        try {
            wishlistService.deleteItem(wishlistId, itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Wishlist item deleted successfully.");
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return redirectToBooks(wishlistId);
    }

    /**
     * Verifies that required English fields are present for catalog books.
     */
    private boolean isValidEnglishFields(BookForm form) {
        return StringUtils.hasText(form.getBookNameEn()) && StringUtils.hasText(form.getAuthorNameEn());
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
}
