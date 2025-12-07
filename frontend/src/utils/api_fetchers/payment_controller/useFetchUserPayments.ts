import { useEffect } from "react";
import type { PaymentModel } from "../../../models/PaymentModel";
import { payment_controller_endpoints } from "../../apiEndpointsUrlsList";
import { fetchWithRequestId } from "../../fetchWithRequestId";

/**
 * Custom hook to fetch user's payment history
 */
export const useFetchUserPayments = (
    authentication: { isAuthenticated: boolean; token: string },
    setPayments: React.Dispatch<React.SetStateAction<PaymentModel[]>>,
    setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
    setHttpError: React.Dispatch<React.SetStateAction<string | null>>
) => {

    useEffect(() => {
        const fetchPayments = async () => {
            setIsLoading(true);

            if (authentication.isAuthenticated) {
                const endpoint = payment_controller_endpoints.get_user_payments;
                const url = endpoint.url;

                const requestOptions = {
                    method: endpoint.method,
                    headers: {
                        Authorization: `Bearer ${authentication.token}`,
                        "Content-type": "application/json"
                    }
                };

                try {
                    const response = await fetchWithRequestId(url, requestOptions);

                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(errorData.message || "Failed to fetch payments");
                    }

                    const paymentsData: PaymentModel[] = await response.json();
                    setPayments(paymentsData);
                } catch (error: any) {
                    setHttpError(error.message);
                } finally {
                    setIsLoading(false);
                }
            } else {
                setIsLoading(false);
            }
        };

        fetchPayments();
    }, [authentication.isAuthenticated, authentication.token]);
};
