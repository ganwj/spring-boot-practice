package com.example.crud.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.crud.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByUserId(String userId);
    List<Book> findByUserIdAndTitleContaining(String userId, String title);
}
