package dev.meirong.showcase.bookstore.controllers;

import dev.meirong.showcase.bookstore.dto.*;
import dev.meirong.showcase.bookstore.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Controller", description = "APIs for payment operations with Stripe integration")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/secure/create-payment-intent")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Create a payment intent", description = "Creates a Stripe payment intent for processing payment")
    public ResponseEntity<CreatePaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request,
            Principal principal) {

        // Ensure user can only create payment for themselves
        if (!request.getUserEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        CreatePaymentIntentResponse response = paymentService.createPaymentIntent(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/secure/confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Confirm payment", description = "Confirms a payment after successful Stripe transaction")
    public ResponseEntity<PaymentDTO> confirmPayment(
            @Valid @RequestBody ConfirmPaymentRequest request,
            Principal principal) {

        // Ensure user can only confirm their own payment
        if (!request.getUserEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        PaymentDTO payment = paymentService.confirmPayment(request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/secure/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its ID")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id, Principal principal) {
        PaymentDTO payment = paymentService.getPaymentById(id);

        // Users can only view their own payments, admins can view all
        if (!payment.getUserEmail().equals(principal.getName()) &&
            !principal.getName().contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/secure/user")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all payments for current user", description = "Retrieves all payments for the authenticated user")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForCurrentUser(Principal principal) {
        List<PaymentDTO> payments = paymentService.getPaymentsByUserEmail(principal.getName());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/secure/user/paginated")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get paginated payments for current user", description = "Retrieves paginated payments for the authenticated user")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsForCurrentUserPaginated(
            Pageable pageable,
            Principal principal) {

        Page<PaymentDTO> payments = paymentService.getPaymentsByUserEmail(principal.getName(), pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/secure/user/pending-amount")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get pending payment amount", description = "Gets total amount of pending payments for current user")
    public ResponseEntity<BigDecimal> getPendingPaymentAmount(Principal principal) {
        BigDecimal amount = paymentService.getPendingPaymentAmount(principal.getName());
        return ResponseEntity.ok(amount);
    }

    @PostMapping("/secure/admin/{id}/refund")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Refund a payment", description = "Refunds a completed payment (Admin only)")
    public ResponseEntity<PaymentDTO> refundPayment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        PaymentDTO payment = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(payment);
    }
}
