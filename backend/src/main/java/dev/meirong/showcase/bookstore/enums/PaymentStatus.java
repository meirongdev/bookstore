package dev.meirong.showcase.bookstore.enums;

/**
 * Payment status enumeration representing the various states of a payment transaction.
 */
public enum PaymentStatus {
    /**
     * Payment intent created, awaiting payment
     */
    PENDING,

    /**
     * Payment is being processed by the payment gateway
     */
    PROCESSING,

    /**
     * Payment completed successfully
     */
    SUCCEEDED,

    /**
     * Payment failed due to insufficient funds, card declined, etc.
     */
    FAILED,

    /**
     * Payment was canceled by the user or system
     */
    CANCELED,

    /**
     * Payment was fully refunded
     */
    REFUNDED,

    /**
     * Payment was partially refunded
     */
    PARTIALLY_REFUNDED
}
