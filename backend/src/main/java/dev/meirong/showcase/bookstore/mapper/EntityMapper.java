package dev.meirong.showcase.bookstore.mapper;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.BookLoanDTO;
import dev.meirong.showcase.bookstore.dto.DiscussionDTO;
import dev.meirong.showcase.bookstore.dto.GenreDTO;
import dev.meirong.showcase.bookstore.dto.PaymentDTO;
import dev.meirong.showcase.bookstore.dto.ReviewDTO;
import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.BookLoan;
import dev.meirong.showcase.bookstore.entities.Discussion;
import dev.meirong.showcase.bookstore.entities.Genre;
import dev.meirong.showcase.bookstore.entities.Payment;
import dev.meirong.showcase.bookstore.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.Instant;

/**
 * MapStruct mapper for entity-DTO conversions.
 * This mapper is automatically implemented by MapStruct at compile time.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EntityMapper {

    // Book mappings
    BookDTO toBookDTO(Book book);

    @Mapping(target = "checkouts", ignore = true)
    @Mapping(target = "bookLoans", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toBook(BookDTO bookDTO);

    // Review mappings
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ReviewDTO toReviewDTO(Review review);

    @Mapping(target = "userLastName", ignore = true)
    @Mapping(target = "reviewedBook", ignore = true)
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    Review toReview(ReviewDTO reviewDTO);

    // Genre mappings
    GenreDTO toGenreDTO(Genre genre);

    @Mapping(target = "books", ignore = true)
    Genre toGenre(GenreDTO genreDTO);

    // Discussion mappings
    @Mapping(source = "discussionHolder.email", target = "userEmail")
    @Mapping(source = "discussionHolder.firstName", target = "userFirstName")
    @Mapping(source = "discussionHolder.lastName", target = "userLastName")
    DiscussionDTO toDiscussionDTO(Discussion discussion);

    @Mapping(target = "discussionHolder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Discussion toDiscussion(DiscussionDTO discussionDTO);

    // BookLoan mappings
    @Mapping(source = "book", target = "bookDTO")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    BookLoanDTO toBookLoanDTO(BookLoan bookLoan);

    @Mapping(source = "bookDTO", target = "book")
    @Mapping(target = "borrower", ignore = true)
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    BookLoan toBookLoan(BookLoanDTO bookLoanDTO);

    // Payment mappings
    @Mapping(source = "paymentHolder.email", target = "userEmail")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    PaymentDTO toPaymentDTO(Payment payment);

    @Mapping(source = "userEmail", target = "paymentHolder.email")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toPayment(PaymentDTO paymentDTO);

    // Timestamp conversion helpers
    default Long instantToLong(Instant instant) {
        return instant == null ? null : instant.toEpochMilli();
    }

    default Instant longToInstant(Long timestamp) {
        return timestamp == null ? null : Instant.ofEpochMilli(timestamp);
    }
}
