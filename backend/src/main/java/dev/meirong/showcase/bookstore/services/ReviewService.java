package dev.meirong.showcase.bookstore.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.meirong.showcase.bookstore.dto.ReviewDTO;
import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.Review;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.BookRepository;
import dev.meirong.showcase.bookstore.repositories.ReviewRepository;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final EntityMapper entityMapper;
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public Page<ReviewDTO> findAllByBookId(Long bookId, Pageable pageable, boolean latest) {

        Book book = getBookFromRepository(bookId);

        Page<Review> reviews;

        if (latest) reviews = reviewRepository.findAllByReviewedBookOrderByIdDesc(book, pageable);
        else reviews = reviewRepository.findByReviewedBook(book, pageable);

        return reviews.map(this::convertToReviewDTO);
    }

    public Double getAverageRatingByBookId(Long bookId) {

        Book book = getBookFromRepository(bookId);

        Double rating = reviewRepository.getAverageRatingByReviewedBook(book);

        if (rating == null) return 0.0;

        return rating;
    }

    private Book getBookFromRepository(Long bookId) {

        Optional<Book> book = bookRepository.findById(bookId);

        if (book.isEmpty()) {
            ErrorsUtil.returnBookError("Book not found", null, HttpStatus.NOT_FOUND);
        }

        return book.get();
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        return entityMapper.toReviewDTO(review);
    }
}
