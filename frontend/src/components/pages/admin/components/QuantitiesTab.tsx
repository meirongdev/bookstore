import { useState } from "react";
import { BookModel } from "../../../../models/BookModel";
import { useFetchBooks } from "../../../../utils/api_fetchers/book_controller/useFetchBooks";
import { Pagination } from "../../../commons/pagination/Pagination";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { QuantitiesTabBookCard } from "./QuantitiesTabBookCard";
import { PaginatedItemsCount } from "../../../commons/pagination/PaginatedItemsCount";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

export const QuantitiesTab = () => {

    const [books, setBooks] = useState<BookModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);
    const [isBookDeleted, setIsBookDeleted] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const [totalAmountOfBooks, setTotalAmountOfBooks] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [resultRange, setResultRange] = useState({start: 1, end: 5});

    const [titleQuery, setTitleQuery] = useState("");
    const [searchParams, setSearchParams] = useState("");

    const handleSearchClick = () => {

        setHttpError(null);
        setSearchParams(`?title-query=${titleQuery}`);
    };

    useFetchBooks(currentPage, setBooks, setIsLoading, setHttpError, setTotalAmountOfBooks, setTotalPages, 5, searchParams, isBookDeleted);

    const renderContent = () => {
        if (isLoading) {
            return <LoadingSpinner />;
        }

        if (httpError) {
            return <HttpErrorMessage httpError={httpError} />;
        }

        if (totalAmountOfBooks === 0) {
            return (
                <div className="text-center py-12 text-gray-600">
                    <p className="text-lg">No books found</p>
                    <p className="text-sm mt-2">Try adjusting your search criteria</p>
                </div>
            );
        }

        return (
            <>
                <PaginatedItemsCount
                    itemsName={"Books"}
                    totalAmountOfItems={totalAmountOfBooks}
                    resultRange={resultRange}
                />

                <div className="flex flex-col gap-4">
                    {books.map(book => (
                        <QuantitiesTabBookCard
                            key={book.id}
                            book={book}
                            setIsBookDeleted={setIsBookDeleted}
                        />
                    ))}
                </div>
            </>
        );
    };

    return (

        <div className="card">

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Manage Quantities</h2>

            <div className="flex gap-3 mb-6">
                <input
                    className="input flex-1"
                    placeholder="Search books by title..."
                    value={titleQuery}
                    onChange={event => setTitleQuery(event.target.value)}
                    onKeyDown={e => e.key === 'Enter' && handleSearchClick()}
                />
                <button className="btn-primary px-6" onClick={handleSearchClick}>
                    Search
                </button>
            </div>

            {renderContent()}

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                totalAmountOfItems={totalAmountOfBooks}
                setCurrentPage={setCurrentPage}
                setResultRange={setResultRange}
            />

        </div>

    )

}
