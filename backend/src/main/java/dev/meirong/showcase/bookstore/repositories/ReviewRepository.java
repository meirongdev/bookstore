package dev.meirong.showcase.bookstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.Review;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByReviewedBookOrderByIdDesc(Book book, Pageable pageable);

    Page<Review> findByReviewedBook(Book book, Pageable pageable);

    Optional<Review> findByUserEmailAndReviewedBook(String userEmail, Book book);

    @Query("select avg(r.rating) from Review r where r.reviewedBook = :book")
    Double getAverageRatingByReviewedBook(@Param("book") Book book);
}
