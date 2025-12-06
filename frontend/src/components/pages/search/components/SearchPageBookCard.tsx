import { Link } from "react-router-dom"
import { BookModel } from "../../../../models/BookModel"
import { BookGenres } from "../../../commons/book_genres/BookGenres"

type BookCardProps = {
    book: BookModel
}

export const SearchPageBookCard = ({ book }: BookCardProps) => {

    return (

        <div className="book-card">

            <img
                src={book.img}
                alt={book.title}
                className="w-32 h-48 object-cover rounded"
            />

            <div className="flex-1 flex flex-col gap-3">

                <div>
                    <h3 className="text-lg font-semibold text-gray-800">{book.title}</h3>
                    <p className="text-sm text-gray-600">{book.author}</p>
                </div>

                <BookGenres genres={book.genres} />

                <p className="text-sm text-gray-600 line-clamp-3">{book.description}</p>

            </div>

            <Link to={`/book/${book.id}`} className="btn-secondary self-start md:self-center">
                View Details
            </Link>

        </div>

    )

}
