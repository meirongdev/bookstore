import { useEffect } from "react";
import { BookLoanHistoryModel } from "../../../models/BookLoanHistoryModel";
import { book_loan_controller_endpoints } from "../../apiEndpointsUrlsList";

/**
 * Custom hook to fetch user's book borrowing history (book loan histories).
 * Retrieves paginated records of books borrowed and returned by the authenticated user.
 */
export const useFetchBookLoanHistories = (
    authentication: { isAuthenticated: boolean; token: string; },
    setBookLoanHistories: React.Dispatch<React.SetStateAction<BookLoanHistoryModel[]>>,
    setTotalAmountOfRecords: React.Dispatch<React.SetStateAction<number>>,
    setTotalPages: React.Dispatch<React.SetStateAction<number>>,
    setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
    setHttpError: React.Dispatch<React.SetStateAction<string | null>>,
    currentPage: number
) => {

    useEffect(

        () => {

            const fetchUserBookLoanHistories = async () => {

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

                    const response = await fetch(url, requestOptions);

                    const responseJson = await response.json();

                    if (!response.ok) {
                        throw new Error(responseJson.message ? responseJson.message : "Oops, something went wrong!");
                    }

                    setTotalAmountOfRecords(responseJson.totalElements);
                    setTotalPages(responseJson.totalPages);

                    const responseRecordsContentArray = responseJson.content;

                    const loadedBookLoanHistories: BookLoanHistoryModel[] = [];

                    for (const key in responseRecordsContentArray) {

                        loadedBookLoanHistories.push(responseRecordsContentArray[key]);
                    }

                    setBookLoanHistories(loadedBookLoanHistories);
                    setIsLoading(false);
                };

                setIsLoading(false);
            };

            fetchUserBookLoanHistories().catch(

                (error: any) => {

                    setIsLoading(false);
                    setHttpError(error.message);
                }
            )

        }, [currentPage]

    );

}
