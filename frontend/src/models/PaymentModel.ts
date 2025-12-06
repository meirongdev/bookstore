export type PaymentStatus =
    | 'PENDING'
    | 'PROCESSING'
    | 'SUCCEEDED'
    | 'FAILED'
    | 'CANCELED'
    | 'REFUNDED'
    | 'PARTIALLY_REFUNDED';

export interface PaymentModel {
    id: number;
    userEmail: string;
    amount: number;
    currency: string;
    stripePaymentIntentId?: string;
    status: PaymentStatus;
    paymentMethodType?: string;
    bookId?: number;
    description?: string;
    receiptUrl?: string;
    failureMessage?: string;
    createdAt: number;
    updatedAt: number;
}

export interface CreatePaymentIntentRequest {
    amount: number;
    currency: string;
    userEmail: string;
    bookId?: number;
    description?: string;
}

export interface CreatePaymentIntentResponse {
    clientSecret: string;
    paymentIntentId: string;
    paymentId: number;
}

export interface ConfirmPaymentRequest {
    paymentIntentId: string;
    userEmail: string;
}
