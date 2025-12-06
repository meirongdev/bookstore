package dev.meirong.showcase.bookstore.dto;

import dev.meirong.showcase.bookstore.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Payment entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private String userEmail;
    private BigDecimal amount;
    private String currency;
    private String stripePaymentIntentId;
    private PaymentStatus status;
    private String paymentMethodType;
    private Long bookId;
    private String description;
    private String receiptUrl;
    private String failureMessage;
    private Long createdAt;
    private Long updatedAt;
}
