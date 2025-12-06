import { useState } from "react";
import { useParams } from "react-router-dom";
import { BookModel } from "../../../models/BookModel";
import { useFetchBook } from "../../../utils/api_fetchers/book_controller/useFetchBook";
import { BookPageBookCard } from "./components/BookPageBookCard";
import { LoadingSpinner } from "../../commons/loading_spinner/LoadingSpinner";
import { useFetchBookReviews } from "../../../utils/api_fetchers/review_controller/useFetchBookReviews";
import { ReviewModel } from "../../../models/ReviewModel";
import { LatestReviews } from "./components/LatestReviews";
import { HttpErrorMessage } from "../../commons/http_error_message/HttpErrorMessage";

export const BookPage = () => {

    const { bookId } = useParams<{ bookId: string }>();

    // Book state
    const [book, setBook] = useState<BookModel>({ id: 0, title: "", author: "", description: "", copies: 0, copiesAvailable: 0, genres: [], img: "" });
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);

    // Book Reviews state
    const [reviews, setReviews] = useState<ReviewModel[]>([]);
    const [totalAmountOfReviews, setTotalAmountOfReviews] = useState(0);
    const [isLoadingReviews, setIsLoadingReviews] = useState(true);
    const [reviewsHttpError, setReviewsHttpError] = useState<string | null>(null);

    const urlPaginationParams = `?page=0&reviews-per-page=3&latest=true`;

    useFetchBook(bookId || "", setBook, setIsLoading, setHttpError);

    useFetchBookReviews(bookId || "", setReviews, setIsLoadingReviews, setReviewsHttpError, setTotalAmountOfReviews, urlPaginationParams, 0);

    if (!bookId) {
        return (
            <div className="page-container">
                <HttpErrorMessage httpError="Book ID not found" />
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="page-container">
                <LoadingSpinner />
            </div>
        );
    }

    if (httpError) {
        return (
            <div className="page-container">
                <HttpErrorMessage httpError={httpError} />
            </div>
        );
    }

    return (

        <div className="page-container">

            <BookPageBookCard book={book} />

            <div className="divider" />

            <LatestReviews
                bookId={bookId}
                reviews={reviews}
                totalAmountOfReviews={totalAmountOfReviews}
                isLoadingReviews={isLoadingReviews}
                reviewsHttpError={reviewsHttpError}
            />

        </div>

    )

}
