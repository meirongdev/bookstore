import { Link, Navigate } from "react-router-dom";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";
import { useState } from "react";
import { useFetchPendingAmount } from "../../../utils/api_fetchers/payment_controller/useFetchPendingAmount";
import { LoadingSpinner } from "../../commons/loading_spinner/LoadingSpinner";
import { HttpErrorMessage } from "../../commons/http_error_message/HttpErrorMessage";
import { CardElement, useElements, useStripe } from "@stripe/react-stripe-js";
import { useProcessPayment } from "../../../utils/api_fetchers/payment_controller/useProcessPayment";
import { FormLoader } from "../../commons/form_loader/FormLoader";

export const PaymentPage = () => {

    const { authentication } = useAuthenticationContext();
    const elements = useElements();
    const stripe = useStripe();

    const [pendingAmount, setPendingAmount] = useState(0);
    const [isLoadingPendingAmount, setIsLoadingPendingAmount] = useState(false);
    const [pendingAmountHttpError, setPendingAmountHttpError] = useState<string | null>(null);
    const [submitPaymentHttpError, setSubmitPaymentHttpError] = useState<string | null>(null);
    const [isProcessingPayment, setIsProcessingPayment] = useState(false);
    const [paymentSuccess, setPaymentSuccess] = useState(false);

    useFetchPendingAmount(authentication, setPendingAmount, setIsLoadingPendingAmount, setPendingAmountHttpError);

    if (!authentication.isAuthenticated) {
        return <Navigate to={"/"} />
    }

    const handlePayClick = async () => {
        await useProcessPayment(
            authentication,
            elements,
            stripe,
            pendingAmount,
            setIsProcessingPayment,
            setSubmitPaymentHttpError,
            () => {
                setPaymentSuccess(true);
                setPendingAmount(0);
            }
        );
    }

    const renderContent = () => {
        if (isLoadingPendingAmount) {
            return <LoadingSpinner />;
        }

        if (pendingAmountHttpError) {
            return <HttpErrorMessage httpError={pendingAmountHttpError} />;
        }

        if (paymentSuccess) {
            return (
                <div className="card text-center">
                    <div className="text-green-600 text-5xl mb-4">✓</div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">Payment Successful!</h2>
                    <p className="text-xl text-gray-700 mb-6">Your payment has been processed successfully.</p>
                    <Link to={'/search'} className="btn-primary inline-block px-8">
                        Browse Books
                    </Link>
                </div>
            );
        }

        if (pendingAmount === 0) {
            return (
                <div className="card text-center">
                    <div className="text-green-600 text-5xl mb-4">✓</div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">All Clear!</h2>
                    <p className="text-xl text-gray-700 mb-6">You have no outstanding fees.</p>
                    <Link to={'/search'} className="btn-primary inline-block px-8">
                        Browse Books
                    </Link>
                </div>
            );
        }

        return (
            <div className="card max-w-2xl mx-auto">
                <h2 className="text-2xl font-bold text-gray-800 mb-6">Pay Outstanding Fees</h2>

                {/* Test Info Banner */}
                <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6">
                    <div className="flex items-start">
                        <div className="flex-shrink-0">
                            <span className="text-2xl">⚠️</span>
                        </div>
                        <div className="ml-3">
                            <h3 className="text-sm font-medium text-yellow-800 mb-2">
                                Test Mode - Do Not Use Real Card Information
                            </h3>
                            <div className="text-sm text-yellow-700 space-y-1">
                                <p><strong>Card Number:</strong> 4242 4242 4242 4242</p>
                                <p><strong>Expiration:</strong> Any future date</p>
                                <p><strong>CVC:</strong> Any 3 digits</p>
                                <p><strong>Postal Code:</strong> Any valid code</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Payment Amount */}
                <div className="bg-gray-50 border border-gray-200 rounded-lg p-6 mb-6">
                    <div className="flex justify-between items-center">
                        <span className="text-lg text-gray-700">Outstanding Fees:</span>
                        <span className="text-3xl font-bold text-red-600">${pendingAmount.toFixed(2)}</span>
                    </div>
                </div>

                {/* Error Message */}
                {submitPaymentHttpError && (
                    <div className="mb-6">
                        <HttpErrorMessage httpError={submitPaymentHttpError} />
                    </div>
                )}

                {/* Payment Form */}
                <div className="space-y-4">
                    <FormLoader isLoading={isProcessingPayment} />

                    <div>
                        <span className="block text-sm font-medium text-gray-700 mb-2">
                            Credit Card Information
                        </span>
                        <div className="input">
                            <CardElement
                                options={{
                                    style: {
                                        base: {
                                            fontSize: '16px',
                                            color: '#374151',
                                            '::placeholder': {
                                                color: '#9CA3AF',
                                            },
                                        },
                                    },
                                }}
                            />
                        </div>
                    </div>

                    <button
                        className="btn-primary w-full"
                        disabled={isProcessingPayment || !stripe || !elements}
                        onClick={handlePayClick}
                    >
                        {isProcessingPayment ? 'Processing...' : `Pay $${pendingAmount.toFixed(2)}`}
                    </button>

                    <p className="text-sm text-gray-500 text-center">
                        🔒 Secure payment powered by Stripe
                    </p>
                </div>
            </div>
        );
    };

    return (
        <div className="page-container">
            <div className="container-centered px-5">
                <h1 className="text-3xl font-bold text-gray-800 mb-6 text-center">Payment</h1>
                {renderContent()}
            </div>
        </div>
    )
}
