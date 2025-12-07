import { useEffect } from "react";
import { payment_controller_endpoints } from "../../apiEndpointsUrlsList";
import { fetchWithRequestId } from "../../fetchWithRequestId";

/**
 * Custom hook to fetch pending payment amount for the authenticated user
 */
export const useFetchPendingAmount = (
    authentication: { isAuthenticated: boolean; token: string },
    setPendingAmount: React.Dispatch<React.SetStateAction<number>>,
    setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
    setHttpError: React.Dispatch<React.SetStateAction<string | null>>
) => {

    useEffect(() => {
        const fetchPendingAmount = async () => {
            setIsLoading(true);

            if (authentication.isAuthenticated) {
                const endpoint = payment_controller_endpoints.get_pending_amount;
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
                        throw new Error(errorData.message || "Failed to fetch pending amount");
                    }

                    const amount = await response.json();
                    setPendingAmount(amount);
                } catch (error: any) {
                    setHttpError(error.message);
                } finally {
                    setIsLoading(false);
                }
            } else {
                setIsLoading(false);
            }
        };

        fetchPendingAmount();
    }, [authentication.isAuthenticated, authentication.token]);
};
