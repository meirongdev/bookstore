package dev.meirong.showcase.bookstore.repositories;

import dev.meirong.showcase.bookstore.entities.Payment;
import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by payment holder (user)
     */
    Optional<Payment> findByPaymentHolder(User paymentHolder);

    /**
     * Find payment by Stripe payment intent ID
     */
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Find all payments by user email
     */
    List<Payment> findByPaymentHolderEmail(String email);

    /**
     * Find all payments by user email with pagination
     */
    Page<Payment> findByPaymentHolderEmail(String email, Pageable pageable);

    /**
     * Find all payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find all payments by user and status
     */
    List<Payment> findByPaymentHolderEmailAndStatus(String email, PaymentStatus status);

    /**
     * Find payments by book ID
     */
    List<Payment> findByBookId(Long bookId);

    /**
     * Find payments by amount and status
     */
    List<Payment> findByAmountAndStatus(BigDecimal amount, PaymentStatus status);
}
