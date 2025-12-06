package dev.meirong.showcase.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for confirming a payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmPaymentRequest {

    @NotBlank(message = "Payment intent ID is required")
    private String paymentIntentId;

    @NotBlank(message = "User email is required")
    private String userEmail;
}
