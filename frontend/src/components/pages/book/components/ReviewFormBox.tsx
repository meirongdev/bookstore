import { useState } from "react";
import { ReviewStars } from "../../../commons/review_stars/ReviewStars"
import { ratings } from "../../../../constants/constants";
import { ReviewModel } from "../../../../models/ReviewModel";
import { FieldErrors } from "../../../commons/field_errors/FieldErrors";

type ReviewFormBoxProps = {
    handleSubmitReviewClick: (review: ReviewModel) => Promise<void>,
    userReviewSubmitHttpError: string | null
}

export const ReviewFormBox = ({ handleSubmitReviewClick, userReviewSubmitHttpError }: ReviewFormBoxProps) => {

    const [review, setReview] = useState<ReviewModel>({ userEmail: "", userFirstName: "", date: new Date(), rating: 0, reviewDescription: "" });

    const handleRatingChange = (value: string) => {

        setReview({ ...review, rating: Number(value) });
    };

    const handleCommentChange = (value: string) => {

        setReview({ ...review, reviewDescription: value });
    };

    const handleSubmit = () => {

        handleSubmitReviewClick(review);
    }

    return (

        <div className="bg-gray-50 border-2 border-gray-200 rounded-lg p-6">

            <h3 className="text-lg font-semibold text-gray-800 mb-4">Leave Your Review</h3>

            <div className="space-y-4">

                {/* Rating Section */}
                <div className="mb-6">
                    <FieldErrors fieldName={"rating"} httpError={userReviewSubmitHttpError} />

                    <span className="block text-sm font-medium text-gray-700 mb-3">
                        Your Rating
                    </span>

                    <div className="flex flex-col gap-3">
                        {/* Star Display */}
                        <div className="flex items-center gap-2">
                            <ReviewStars ratingProp={review.rating} size={28} />
                            <span className="text-sm text-gray-600">
                                {review.rating > 0 ? `${review.rating}.0 / 5.0` : 'Not rated yet'}
                            </span>
                        </div>

                        {/* Rating Selector */}
                        <select
                            className="dropdown w-full sm:w-auto"
                            value={review.rating}
                            onChange={event => handleRatingChange(event.target.value)}
                        >
                            <option disabled value="0">Select Rating</option>
                            {ratings.map(rating => (
                                <option key={rating.id} value={rating.value}>
                                    {rating.name}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* Comment Section */}
                <div>
                    <span className="block text-sm font-medium text-gray-700 mb-2">
                        Your Comment (Optional)
                    </span>
                    <textarea
                        className="input min-h-[100px]"
                        rows={4}
                        placeholder="Share your thoughts about this book..."
                        value={review.reviewDescription}
                        onChange={event => handleCommentChange(event.target.value)}
                    />
                </div>

                {/* Submit Button */}
                <button
                    className="btn-primary w-full"
                    onClick={handleSubmit}
                    disabled={review.rating === 0}
                >
                    Submit Review
                </button>

            </div>

        </div>

    )

}
