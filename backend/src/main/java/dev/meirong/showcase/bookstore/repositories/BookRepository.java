package dev.meirong.showcase.bookstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.Genre;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitleAndAuthor(String title, String author); // This method is required for BookValidator

    Page<Book> findByTitleContainingIgnoreCase(String query, Pageable pageable);

    Page<Book> findByGenresContains(Genre genre, Pageable pageable);
}
