package dev.meirong.showcase.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for payment intent creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private Long paymentId;
}
