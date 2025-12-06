import { ReviewModel } from "../../../../models/ReviewModel";
import { ReviewCard } from "../../../commons/review_card/ReviewCard";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { Link } from "react-router-dom";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

type LatestReviewsProps = {
    bookId: string
    reviews: ReviewModel[],
    totalAmountOfReviews: number,
    isLoadingReviews: boolean,
    reviewsHttpError: string | null,
}

export const LatestReviews = ({ bookId, reviews, totalAmountOfReviews, isLoadingReviews, reviewsHttpError }: LatestReviewsProps) => {

    return (

        <div className="space-y-4">

            <h2 className="text-xl font-semibold text-gray-800">
                Reviews {totalAmountOfReviews > 0 && `(${totalAmountOfReviews})`}
            </h2>

            {totalAmountOfReviews === 0 ? (
                <p className="text-center text-gray-600 py-8">
                    No reviews yet. Be the first to review this book!
                </p>
            ) : isLoadingReviews ? (
                <LoadingSpinner />
            ) : reviewsHttpError ? (
                <HttpErrorMessage httpError={reviewsHttpError} />
            ) : (
                <div className="space-y-4">
                    {reviews.map(review => (
                        <ReviewCard key={review.id} review={review} />
                    ))}

                    {totalAmountOfReviews > 3 && (
                        <Link to={`/reviews/${bookId}`} className="btn-secondary inline-block">
                            View All Reviews ({totalAmountOfReviews})
                        </Link>
                    )}
                </div>
            )}

        </div>

    )

}
