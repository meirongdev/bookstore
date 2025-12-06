package dev.meirong.showcase.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a payment intent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentIntentRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.50", message = "Amount must be at least 0.50")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO 4217)")
    @Builder.Default
    private String currency = "usd";

    @Email(message = "Invalid email format")
    @NotBlank(message = "User email is required")
    private String userEmail;

    private Long bookId;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
