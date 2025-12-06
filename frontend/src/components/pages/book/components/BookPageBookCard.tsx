import { useState } from "react"
import { BookModel } from "../../../../models/BookModel"
import { ReviewStars } from "../../../commons/review_stars/ReviewStars"
import { CheckoutBox } from "./CheckoutBox"
import { useFetchBookAverageRating } from "../../../../utils/api_fetchers/review_controller/useFetchBookAverageRating"
import { FormLoader } from "../../../commons/form_loader/FormLoader"
import { BookGenres } from "../../../commons/book_genres/BookGenres"
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage"

type BookCardProps = {
    book: BookModel
}

export const BookPageBookCard = ({ book }: BookCardProps) => {

    const [averageRating, setAverageRating] = useState(0);
    const [isLoadingAverageRating, setIsLoadingAverageRating] = useState(true);
    const [averageRatingHttpError, setAverageRatingHttpError] = useState<string | null>(null);
    const [isRatingChanged, setIsRatingChanged] = useState(false);

    const bookId: string = `${book.id}`;

    useFetchBookAverageRating(bookId, setAverageRating, setIsLoadingAverageRating, setAverageRatingHttpError, isRatingChanged);

    const renderRatingSection = () => {
        if (isLoadingAverageRating) {
            return <FormLoader isLoading={isLoadingAverageRating} />;
        }

        if (averageRatingHttpError) {
            return <HttpErrorMessage httpError={averageRatingHttpError} />;
        }

        return (
            <>
                <ReviewStars ratingProp={averageRating} size={24} />
                <span className="text-base text-gray-600 font-medium">
                    {averageRating.toFixed(1)} / 5.0
                </span>
            </>
        );
    };

    return (

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

            {/* 图片区域 */}
            <div className="md:col-span-1">
                <img
                    src={book.img}
                    alt={book.title}
                    className="w-full h-auto rounded-lg shadow-md"
                />
            </div>

            {/* 书籍信息区域 */}
            <div className="md:col-span-2 flex flex-col gap-4">

                <div>
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">{book.title}</h1>
                    <p className="text-xl text-gray-600">{book.author}</p>
                </div>

                <div className="flex items-center gap-2">
                    {renderRatingSection()}
                </div>

                <BookGenres genres={book.genres} />

                <div className="divider" />

                <div>
                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Description</h3>
                    <p className="text-gray-700 leading-relaxed">{book.description}</p>
                </div>

                <div className="divider" />

                {/* Checkout Box */}
                <CheckoutBox book={book} setIsRatingChanged={setIsRatingChanged} />

            </div>

        </div>

    )

}
