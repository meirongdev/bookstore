package dev.meirong.showcase.bookstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.meirong.showcase.bookstore.converters.InstantToLongConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "checkouts")
@EntityListeners(AuditingEntityListener.class)
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    @JsonIgnore
    private User checkoutHolder;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    @JsonIgnoreProperties("checkouts, bookLoans, reviews")
    private Book checkedOutBook;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant createdAt;

    public Checkout(User checkoutHolder, Book checkedOutBook, LocalDate checkoutDate, LocalDate returnDate) {
        this.checkoutHolder = checkoutHolder;
        this.checkedOutBook = checkedOutBook;
        this.checkoutDate = checkoutDate;
        this.returnDate = returnDate;
    }
}
