package dev.meirong.showcase.bookstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.meirong.showcase.bookstore.converters.InstantToLongConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_first_name")
    private String userFirstName;

    @Column(name = "user_last_name")
    private String userLastName;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    @JsonIgnore
    private Book reviewedBook;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant updatedAt;

    @NotNull(message = "Rating must be present")
    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_description")
    private String reviewDescription;

    public Review(String userEmail, String userFirstName, String userLastName, Book reviewedBook, Double rating, String reviewDescription) {
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.reviewedBook = reviewedBook;
        this.rating = rating;
        this.reviewDescription = reviewDescription;
    }
}
