package com.example.crud.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "books")
@Setter
@Getter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_id_seq")
    @SequenceGenerator(name = "book_id_seq", sequenceName = "books_seq", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Title is mandatory")
    @Size(max = 50, message = "Title should not be greater than 50 characters")
    private String title;

    @Column(name = "author", nullable = false)
    @NotBlank(message = "Author is mandatory")
    @Size(max = 50, message = "Author should not be greater than 50 characters")
    private String author;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Description is mandatory")
    @Size(max = 700, message = "Description should not be greater than 700 characters")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @Column(name = "user_id", updatable = false)
    private String userId;

    @PrePersist
    private void onCreate() {
        createdAt = new Date();
    }

    public Book(String title, String author, String description) {
        this.title = title;
        this.author = author;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + title + ", author=" + author + ", description=" + description + ", createdAt=" + createdAt + "]";
    }
}
