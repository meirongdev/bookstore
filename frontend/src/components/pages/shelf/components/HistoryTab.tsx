import { useState } from "react";
import { useAuthenticationContext } from "../../../../authentication/authenticationContext";
import { BookLoanHistoryModel } from "../../../../models/BookLoanHistoryModel";
import { useFetchBookLoanHistories } from "../../../../utils/api_fetchers/book_loan_controller/useFetchBookLoanHistories";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { Link } from "react-router-dom";
import { Pagination } from "../../../commons/pagination/Pagination";
import { HistoryTabRecordCard } from "./HistoryTabRecordCard";
import { PaginatedItemsCount } from "../../../commons/pagination/PaginatedItemsCount";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

export const HistoryTab = () => {

    const { authentication } = useAuthenticationContext();

    const [bookLoanHistories, setBookLoanHistories] = useState<BookLoanHistoryModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [totalAmountOfRecords, setTotalAmountOfRecords] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [resultRange, setResultRange] = useState({start: 1, end: 5});

    useFetchBookLoanHistories(authentication, setBookLoanHistories, setTotalAmountOfRecords, setTotalPages, setIsLoading, setHttpError, currentPage);

    const renderContent = () => {
        if (isLoading) {
            return <LoadingSpinner />;
        }

        if (httpError) {
            return <HttpErrorMessage httpError={httpError} />;
        }

        if (bookLoanHistories.length === 0) {
            return (
                <div className="text-center py-12">
                    <p className="text-xl text-gray-700 mb-4">No borrowing history yet.</p>
                    <p className="text-gray-600 mb-6">Check out some books to start building your reading history!</p>
                    <Link to={'/search'} className="btn-primary inline-block px-8">
                        Browse Books
                    </Link>
                </div>
            );
        }

        return (
            <>
                <PaginatedItemsCount
                    itemsName={"Borrowing Records"}
                    totalAmountOfItems={totalAmountOfRecords}
                    resultRange={resultRange}
                />

                <div className="flex flex-col gap-4">
                    {bookLoanHistories.map((loan: BookLoanHistoryModel) => (
                        <HistoryTabRecordCard key={loan.id} loan={loan} />
                    ))}
                </div>

                <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    totalAmountOfItems={totalAmountOfRecords}
                    setCurrentPage={setCurrentPage}
                    setResultRange={setResultRange}
                />
            </>
        );
    };

    return (

        <div className="card">

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Borrowing History</h2>

            {renderContent()}

        </div>

    )

}
