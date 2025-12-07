import { useEffect } from "react";
import { BookLoanModel } from "../../../models/BookLoanModel";
import { book_loan_controller_endpoints } from "../../apiEndpointsUrlsList";
import { fetchWithRequestId } from "../../fetchWithRequestId";

/**
 * Custom hook to fetch user's book borrowing history (book loans).
 * Retrieves paginated records of books borrowed and returned by the authenticated user.
 */
export const useFetchBookLoans = (
    authentication: { isAuthenticated: boolean; token: string; },
    setBookLoans: React.Dispatch<React.SetStateAction<BookLoanModel[]>>,
    setTotalAmountOfRecords: React.Dispatch<React.SetStateAction<number>>,
    setTotalPages: React.Dispatch<React.SetStateAction<number>>,
    setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
    setHttpError: React.Dispatch<React.SetStateAction<string | null>>,
    currentPage: number
) => {

    useEffect(

        () => {

            const fetchUserBookLoans = async () => {

                setIsLoading(true);

                if (authentication.isAuthenticated) {

                    const urlParams = `?page=${currentPage - 1}&records-per-page=5`;

                    const endpoint = book_loan_controller_endpoints.find_all_book_loans;

                    const url = endpoint.url + urlParams;

                    const requestOptions = {

                        method: endpoint.method,
                        headers: {
                            Authorization: `Bearer ${authentication.token}`,
                            "Content-type": "application/json"
                        }
                    };

                    const response = await fetchWithRequestId(url, requestOptions);

                    const responseJson = await response.json();

                    if (!response.ok) {
                        throw new Error(responseJson.message ? responseJson.message : "Oops, something went wrong!");
                    }

                    setTotalAmountOfRecords(responseJson.totalElements);
                    setTotalPages(responseJson.totalPages);

                    const responseRecordsContentArray = responseJson.content;

                    const loadedBookLoans: BookLoanModel[] = [];

                    for (const key in responseRecordsContentArray) {

                        loadedBookLoans.push(responseRecordsContentArray[key]);
                    }

                    setBookLoans(loadedBookLoans);
                    setIsLoading(false);
                };

                setIsLoading(false);
            };

            fetchUserBookLoans().catch(

                (error: any) => {

                    setIsLoading(false);
                    setHttpError(error.message);
                }
            )

        }, [currentPage]

    );

}