package dev.meirong.showcase.bookstore.entities;

import dev.meirong.showcase.bookstore.converters.InstantToLongConverter;
import dev.meirong.showcase.bookstore.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment entity representing a payment transaction in the bookstore system.
 * Tracks payment status, amount, and integration with Stripe payment gateway.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who made the payment
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User paymentHolder;

    /**
     * Payment amount with precision
     * For example: $10.50
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code (ISO 4217), e.g., "usd", "eur", "gbp"
     */
    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "usd";

    /**
     * Stripe Payment Intent ID
     */
    @Column(name = "stripe_payment_intent_id", unique = true, length = 255)
    private String stripePaymentIntentId;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Payment method type (card, bank_transfer, etc.)
     */
    @Column(name = "payment_method_type", length = 50)
    private String paymentMethodType;

    /**
     * Optional: Book ID if payment is for a specific book purchase
     */
    @Column(name = "book_id")
    private Long bookId;

    /**
     * Optional: Description of what the payment is for
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Receipt URL from Stripe (if available)
     */
    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    /**
     * Failure reason if payment failed
     */
    @Column(name = "failure_message", length = 1000)
    private String failureMessage;

    /**
     * Timestamp when the payment was created
     */
    @Column(name = "created_at", nullable = false)
    @CreatedDate
    @Convert(converter = InstantToLongConverter.class)
    private Instant createdAt;

    /**
     * Timestamp when the payment was last updated
     */
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    @Convert(converter = InstantToLongConverter.class)
    private Instant updatedAt;
}
