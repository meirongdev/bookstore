import { useState } from "react";
import { ReviewModel } from "../../../models/ReviewModel";
import { useFetchBookReviews } from "../../../utils/api_fetchers/review_controller/useFetchBookReviews";
import { Pagination } from "../../commons/pagination/Pagination";
import { ReviewCard } from "../../commons/review_card/ReviewCard";
import { Link } from "react-router-dom";
import { LoadingSpinner } from "../../commons/loading_spinner/LoadingSpinner";
import { PaginatedItemsCount } from "../../commons/pagination/PaginatedItemsCount";
import { HttpErrorMessage } from "../../commons/http_error_message/HttpErrorMessage";

export const ReviewsPage = () => {

    const bookId = (window.location.pathname).split('/')[2];

    const [reviews, setReviews] = useState<ReviewModel[]>([]);
    const [totalAmountOfReviews, setTotalAmountOfReviews] = useState(0);
    const [isLoadingReviews, setIsLoadingReviews] = useState(true);
    const [reviewsHttpError, setReviewsHttpError] = useState<string | null>(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [resultRange, setResultRange] = useState({start: 1, end: 5});

    const urlPaginationParams = `?page=${currentPage - 1}&reviews-per-page=5`;

    useFetchBookReviews(bookId, setReviews, setIsLoadingReviews, setReviewsHttpError, setTotalAmountOfReviews, urlPaginationParams, currentPage, setTotalPages);

    const renderContent = () => {
        if (isLoadingReviews) {
            return <LoadingSpinner />;
        }

        if (reviewsHttpError) {
            return <HttpErrorMessage httpError={reviewsHttpError} />;
        }

        return (
            <>
                <div className="flex items-center justify-between gap-4 flex-wrap">
                    <PaginatedItemsCount
                        itemsName={"Reviews"}
                        totalAmountOfItems={totalAmountOfReviews}
                        resultRange={resultRange}
                    />
                    <Link to={`/book/${bookId}`} className="btn-secondary px-6">
                        ← Back to Book
                    </Link>
                </div>

                <div className="flex flex-col gap-4">
                    {reviews.map(review => (
                        <ReviewCard key={review.id} review={review} />
                    ))}
                </div>

                <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    totalAmountOfItems={totalAmountOfReviews}
                    setCurrentPage={setCurrentPage}
                    setResultRange={setResultRange}
                />
            </>
        );
    };

    return (

        <div className="page-container">

            <div className="container-centered px-5">

                <h1 className="text-3xl font-bold text-gray-800 mb-6">Book Reviews</h1>

                <div className="card">
                    {renderContent()}
                </div>

            </div>

        </div>

    )

}
