package com.example.crud.controller;

import com.example.crud.model.Book;
import com.example.crud.repository.BookRepository;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {

    @Autowired
    BookRepository bookRepository;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("profile", principal.getClaims());
        }

        return "index";
    }

    @GetMapping("/books")
    public String getAllBooks(Model model, @AuthenticationPrincipal OidcUser principal, @Param("title") String keyword) {
        try {
            model.addAttribute("profile", principal.getClaims());
            String userId = principal.getSubject();
            List<Book> books = new ArrayList<Book>();

            if (keyword == null) {
                books.addAll(bookRepository.findByUserId(userId));
            } else {
                books.addAll(bookRepository.findByUserIdAndTitleContaining(userId, keyword));
                model.addAttribute("keyword", keyword);
            }

            model.addAttribute("books", books);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "books";
    }

    @GetMapping("/books/new")
    public String addBook(@NotNull Model model, @AuthenticationPrincipal @NotNull OidcUser principal) {
        model.addAttribute("profile", principal.getClaims());
        Book book = new Book();

        model.addAttribute("book", book);
        model.addAttribute("pageTitle", "Create new book");

        return "book_form";
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid Book book, @AuthenticationPrincipal OidcUser principal, BindingResult result, RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "book_form";
            }

            book.setUserId(principal.getSubject());
            bookRepository.save(book);
            redirectAttributes.addFlashAttribute("message", "The book has been saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }

        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String editBook(@PathVariable("id") long id, Model model, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("profile", principal.getClaims());
            Optional<Book> bookData = bookRepository.findById(id);

            if (bookData.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Book not found.");
                return "redirect:/books";
            }

            Book book = bookData.get();
            String userId = principal.getSubject();

            if (!book.getUserId().equals(userId)) {
                redirectAttributes.addFlashAttribute("message", "You are not authorized to edit this book.");
                return "redirect:/books";
            }
            model.addAttribute("book", book);
            model.addAttribute("pageTitle", "Edit Book (ID: " + id + ")");

            return "book_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/books";
        }
    }

    @PostMapping("/books/update/{id}")
    public String updateBook(@PathVariable("id") long id, @Valid Book book, BindingResult result, RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "book_form";
            }

            Optional<Book> bookData = bookRepository.findById(id);
            if (bookData.isPresent()) {
                Book _book = bookData.get();
                _book.setTitle(book.getTitle());
                _book.setAuthor(book.getAuthor());
                _book.setDescription(book.getDescription());
                bookRepository.save(_book);
                redirectAttributes.addFlashAttribute("message", "The book with id=" + id + " has been updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("message", "Book not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") long id, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        try {
            String userId = principal.getSubject();
            Optional<Book> bookData = bookRepository.findById(id);
            if (bookData.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Book not found.");
                return "redirect:/books";
            }

            if (!bookData.get().getUserId().equals(userId)) {
                redirectAttributes.addFlashAttribute("message", "You are not authorized to delete this book.");
                return "redirect:/books";
            }

            bookRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The Book with id=" + id + " has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/books";
    }
}
