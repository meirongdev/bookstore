import type { Stripe, StripeElements } from "@stripe/stripe-js";
import type { CreatePaymentIntentRequest, CreatePaymentIntentResponse } from "../../../models/PaymentModel";
import { payment_controller_endpoints } from "../../apiEndpointsUrlsList";

/**
 * Extract user email from JWT token
 */
const extractUserEmailFromToken = (token: string): string => {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.error("Invalid JWT format");
            return "";
        }
        const payload = JSON.parse(atob(parts[1]));
        return payload.sub || "";
    } catch (error) {
        console.error("Failed to extract email from token:", error);
        return "";
    }
};

/**
 * Process payment using Stripe
 */
export const useProcessPayment = async (
    authentication: { isAuthenticated: boolean; token: string; userEmail?: string },
    elements: StripeElements | null,
    stripe: Stripe | null,
    amount: number,
    setIsProcessing: React.Dispatch<React.SetStateAction<boolean>>,
    setHttpError: React.Dispatch<React.SetStateAction<string | null>>,
    onSuccess: () => void
) => {
    if (!stripe || !elements || !authentication.isAuthenticated) {
        setHttpError("Payment system not ready");
        return;
    }

    // Extract email from token if not provided
    const userEmail = authentication.userEmail || extractUserEmailFromToken(authentication.token);

    if (!userEmail) {
        setHttpError("User email not found");
        return;
    }

    setIsProcessing(true);
    setHttpError(null);

    try {
        // Step 1: Create Payment Intent
        const createPaymentRequest: CreatePaymentIntentRequest = {
            amount: amount,
            currency: "usd",
            userEmail: userEmail, // Use extracted email instead of token
            description: "Late fee payment"
        };

        const createEndpoint = payment_controller_endpoints.create_payment_intent;
        const createResponse = await fetch(createEndpoint.url, {
            method: createEndpoint.method,
            headers: {
                Authorization: `Bearer ${authentication.token}`,
                "Content-type": "application/json"
            },
            body: JSON.stringify(createPaymentRequest)
        });

        if (!createResponse.ok) {
            const errorData = await createResponse.json();
            throw new Error(errorData.message || "Failed to create payment intent");
        }

        const paymentIntentData: CreatePaymentIntentResponse = await createResponse.json();
        const { clientSecret, paymentIntentId } = paymentIntentData;

        // Step 2: Confirm payment with Stripe
        const cardElement = elements.getElement("card");
        if (!cardElement) {
            throw new Error("Card element not found");
        }

        const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
            payment_method: {
                card: cardElement,
            }
        });

        if (stripeError) {
            throw new Error(stripeError.message || "Payment failed");
        }

        if (paymentIntent?.status === "succeeded") {
            // Step 3: Confirm payment with backend
            const confirmEndpoint = payment_controller_endpoints.confirm_payment;
            const confirmResponse = await fetch(confirmEndpoint.url, {
                method: confirmEndpoint.method,
                headers: {
                    Authorization: `Bearer ${authentication.token}`,
                    "Content-type": "application/json"
                },
                body: JSON.stringify({
                    paymentIntentId: paymentIntentId,
                    userEmail: userEmail // Use extracted email instead of token
                })
            });

            if (!confirmResponse.ok) {
                const errorData = await confirmResponse.json();
                throw new Error(errorData.message || "Failed to confirm payment");
            }

            // Success!
            onSuccess();
        } else {
            throw new Error("Payment was not successful");
        }
    } catch (error: any) {
        console.error("Payment error:", error);
        setHttpError(error.message || "An error occurred during payment");
    } finally {
        setIsProcessing(false);
    }
};
