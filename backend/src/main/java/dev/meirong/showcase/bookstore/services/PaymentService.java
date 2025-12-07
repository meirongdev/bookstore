package dev.meirong.showcase.bookstore.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import dev.meirong.showcase.bookstore.dto.*;
import dev.meirong.showcase.bookstore.entities.Payment;
import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.enums.PaymentStatus;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.PaymentRepository;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling payment operations with Stripe integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    /**
     * Create a new payment intent with Stripe
     */
    @Transactional
    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        try {
            log.info("Creating payment intent for user: {}, amount: {}", request.getUserEmail(), request.getAmount());

            // Validate amount - must be greater than 0
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                ErrorsUtil.returnPaymentError("Payment amount must be greater than zero", HttpStatus.BAD_REQUEST);
            }

            // Validate user
            User user = getUserByEmail(request.getUserEmail());

            // Convert amount to cents (Stripe requires amount in smallest currency unit)
            long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

            // Create Stripe Payment Intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .addPaymentMethodType("card")
                    .setDescription(request.getDescription())
                    .putMetadata("user_email", request.getUserEmail())
                    .putMetadata("book_id", request.getBookId() != null ? request.getBookId().toString() : "")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Save payment record in database
            Payment payment = Payment.builder()
                    .paymentHolder(user)
                    .amount(request.getAmount())
                    .currency(request.getCurrency().toLowerCase())
                    .stripePaymentIntentId(paymentIntent.getId())
                    .status(PaymentStatus.PENDING)
                    .bookId(request.getBookId())
                    .description(request.getDescription())
                    .build();

            payment = paymentRepository.save(payment);

            log.info("Payment intent created successfully: {}", paymentIntent.getId());

            return CreatePaymentIntentResponse.builder()
                    .clientSecret(paymentIntent.getClientSecret())
                    .paymentIntentId(paymentIntent.getId())
                    .paymentId(payment.getId())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: {}", e.getMessage(), e);
            ErrorsUtil.returnPaymentError("Failed to create payment intent: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            return null;
        }
    }

    /**
     * Confirm and process payment after successful Stripe payment
     */
    @Transactional
    public PaymentDTO confirmPayment(ConfirmPaymentRequest request) {
        try {
            log.info("Confirming payment: {}", request.getPaymentIntentId());

            // Retrieve payment intent from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());

            // Find payment in database
            Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                    .orElseThrow(() -> new RuntimeException("Payment not found for intent: " + request.getPaymentIntentId()));

            // Update payment status based on Stripe status
            updatePaymentFromStripeIntent(payment, paymentIntent);

            payment = paymentRepository.save(payment);

            log.info("Payment confirmed successfully: {}, status: {}", payment.getId(), payment.getStatus());

            return entityMapper.toPaymentDTO(payment);

        } catch (StripeException e) {
            log.error("Stripe error confirming payment: {}", e.getMessage(), e);
            ErrorsUtil.returnPaymentError("Failed to confirm payment: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            return null;
        }
    }

    /**
     * Get payment by ID
     */
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return entityMapper.toPaymentDTO(payment);
    }

    /**
     * Get all payments for a user
     */
    public List<PaymentDTO> getPaymentsByUserEmail(String userEmail) {
        List<Payment> payments = paymentRepository.findByPaymentHolderEmail(userEmail);
        return payments.stream()
                .map(entityMapper::toPaymentDTO)
                .toList();
    }

    /**
     * Get paginated payments for a user
     */
    public Page<PaymentDTO> getPaymentsByUserEmail(String userEmail, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByPaymentHolderEmail(userEmail, pageable);
        return payments.map(entityMapper::toPaymentDTO);
    }

    /**
     * Get total amount of pending payments for a user
     */
    public BigDecimal getPendingPaymentAmount(String userEmail) {
        List<Payment> pendingPayments = paymentRepository.findByPaymentHolderEmailAndStatus(
                userEmail, PaymentStatus.PENDING);

        return pendingPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Refund a payment
     */
    @Transactional
    public PaymentDTO refundPayment(Long paymentId, String reason) {
        try {
            log.info("Refunding payment: {}", paymentId);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

            if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
                ErrorsUtil.returnPaymentError("Can only refund succeeded payments", HttpStatus.BAD_REQUEST);
                return null;
            }

            // Create refund in Stripe
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentIntentId())
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .build();

            Refund.create(params);

            // Update payment status
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setDescription(payment.getDescription() + " [Refunded: " + reason + "]");
            payment = paymentRepository.save(payment);

            log.info("Payment refunded successfully: {}", paymentId);

            return entityMapper.toPaymentDTO(payment);

        } catch (StripeException e) {
            log.error("Stripe error refunding payment: {}", e.getMessage(), e);
            ErrorsUtil.returnPaymentError("Failed to refund payment: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            return null;
        }
    }

    /**
     * Handle Stripe webhook events
     */
    @Transactional
    public void handleWebhookEvent(PaymentIntent paymentIntent) {
        log.info("Handling webhook event for payment intent: {}", paymentIntent.getId());

        Optional<Payment> paymentOptional = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId());

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            updatePaymentFromStripeIntent(payment, paymentIntent);
            paymentRepository.save(payment);
            log.info("Payment updated from webhook: {}, status: {}", payment.getId(), payment.getStatus());
        } else {
            log.warn("Payment not found for webhook event: {}", paymentIntent.getId());
        }
    }

    /**
     * Update payment entity from Stripe PaymentIntent
     */
    private void updatePaymentFromStripeIntent(Payment payment, PaymentIntent paymentIntent) {
        String status = paymentIntent.getStatus();

        switch (status) {
            case "succeeded" -> {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                // Receipt URL can be retrieved from payment intent metadata or charges
                if (paymentIntent.getReceiptEmail() != null) {
                    payment.setDescription(payment.getDescription() + " [Receipt sent to: " + paymentIntent.getReceiptEmail() + "]");
                }
            }
            case "processing" -> payment.setStatus(PaymentStatus.PROCESSING);
            case "requires_payment_method", "requires_confirmation", "requires_action" ->
                payment.setStatus(PaymentStatus.PENDING);
            case "canceled" -> payment.setStatus(PaymentStatus.CANCELED);
            default -> {
                payment.setStatus(PaymentStatus.FAILED);
                if (paymentIntent.getLastPaymentError() != null) {
                    payment.setFailureMessage(paymentIntent.getLastPaymentError().getMessage());
                }
            }
        }

        if (paymentIntent.getPaymentMethod() != null) {
            payment.setPaymentMethodType(paymentIntent.getPaymentMethod());
        }
    }

    /**
     * Get user by email
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Clean up zero-amount payment records (data maintenance method)
     * This removes legacy payment records created before the overdue-only payment policy
     */
    @Transactional
    public int cleanupZeroAmountPayments() {
        log.info("Starting cleanup of zero-amount payment records");

        // Find all pending payments with zero amount
        List<Payment> zeroAmountPayments = paymentRepository.findByAmountAndStatus(
                BigDecimal.ZERO, PaymentStatus.PENDING);

        if (!zeroAmountPayments.isEmpty()) {
            log.info("Found {} zero-amount pending payments to delete", zeroAmountPayments.size());
            paymentRepository.deleteAll(zeroAmountPayments);
        }

        log.info("Cleanup completed. Deleted {} zero-amount payment records", zeroAmountPayments.size());
        return zeroAmountPayments.size();
    }
}
