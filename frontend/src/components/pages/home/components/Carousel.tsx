import { useState } from "react"
import { BookModel } from "../../../../models/BookModel";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { useFetchBooks } from "../../../../utils/api_fetchers/book_controller/useFetchBooks";
import { BookGenres } from "../../../commons/book_genres/BookGenres";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";
import { Link } from "react-router-dom";
import { Pagination } from "../../../commons/pagination/Pagination";

export const Carousel = () => {

    const [books, setBooks] = useState<BookModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalAmountOfBooks, setTotalAmountOfBooks] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useFetchBooks(currentPage, setBooks, setIsLoading, setHttpError, setTotalAmountOfBooks, setTotalPages, 12);

    return (

        <>
            {(() => {
                if (isLoading) {
                    return <LoadingSpinner />;
                } else if (httpError) {
                    return <HttpErrorMessage httpError={httpError} />;
                } else {
                    return (
                        <>
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-8">
                                {books.map(book => (
                                    <div key={book.id} className="bg-white border border-gray-200 rounded-lg p-6 shadow hover:shadow-lg transition-shadow flex flex-col h-full">

                                        <img
                                            src={book.img}
                                            alt={book.title}
                                            className="w-full h-96 object-cover rounded-md mb-4"
                                        />

                                        <div className="flex flex-col gap-3 flex-1">

                                            <div>
                                                <h3 className="text-xl font-semibold text-gray-800 line-clamp-2">{book.title}</h3>
                                                <p className="text-base text-gray-600 line-clamp-1">{book.author}</p>
                                            </div>

                                            <BookGenres genres={book.genres} />

                                            <Link to={`/book/${book.id}`} className="btn-secondary w-full text-center mt-auto pt-2 pb-2">
                                                View Details
                                            </Link>

                                        </div>

                                    </div>
                                ))}
                            </div>

                            <div className="mt-6">
                                <Pagination
                                    currentPage={currentPage}
                                    totalPages={totalPages}
                                    totalAmountOfItems={totalAmountOfBooks}
                                    setCurrentPage={setCurrentPage}
                                    setResultRange={() => {}}
                                />
                            </div>
                        </>
                    );
                }
            })()}
        </>

    )

}
