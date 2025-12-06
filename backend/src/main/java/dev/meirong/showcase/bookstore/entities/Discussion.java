package dev.meirong.showcase.bookstore.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.meirong.showcase.bookstore.converters.InstantToLongConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "discussions")
@EntityListeners(AuditingEntityListener.class)
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    @JsonIgnore
    private User discussionHolder;

    @NotBlank(message = "Discussion title must be present and contain at least 1 character")
    @Size(max = 100, message = "Discussion title length must not exceed 100 characters")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Question must be present and contain at least 1 character")
    @Column(name = "question")
    private String question;

    @Column(name = "admin_email")
    private String adminEmail;

    @Column(name = "response")
    private String response;

    @Column(name = "closed")
    private Boolean closed;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Convert(converter = InstantToLongConverter.class)
    private Instant updatedAt;

    public Discussion(User discussionHolder, String title, String question) {
        this.discussionHolder = discussionHolder;
        this.title = title;
        this.question = question;
    }
}
