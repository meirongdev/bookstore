package dev.meirong.showcase.bookstore.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.meirong.showcase.bookstore.converters.InstantToLongConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

/**
 * BookLoan entity represents a historical record of a book checkout/return transaction.
 * This entity tracks when users borrow books and when they return them.
 * Used for generating user's borrowing history and analytics.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "book_loan_histories")
@EntityListeners(AuditingEntityListener.class)
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    @JsonIgnore
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    @JsonIgnore
    private Book book;

    @Column(name = "borrowed_date", nullable = false)
    private LocalDate borrowedDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status = LoanStatus.RETURNED;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant updatedAt;

    public BookLoan(User borrower, Book book, LocalDate borrowedDate, LocalDate returnedDate) {
        this.borrower = borrower;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
        this.status = returnedDate != null ? LoanStatus.RETURNED : LoanStatus.ACTIVE;
    }

    /**
     * Loan status enumeration
     */
    public enum LoanStatus {
        ACTIVE,     // Currently borrowed, not yet returned
        RETURNED,   // Returned successfully
        OVERDUE     // Past due date, not yet returned
    }
}
