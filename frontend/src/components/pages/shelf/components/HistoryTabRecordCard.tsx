import { BookLoanHistoryModel } from "../../../../models/BookLoanHistoryModel"
import { BookGenres } from "../../../commons/book_genres/BookGenres"
import { BookLoanInfoBox } from "./BookLoanInfoBox"

type HistoryTabRecordCardProps = {
    loan: BookLoanHistoryModel
}

export const HistoryTabRecordCard = ({ loan }: HistoryTabRecordCardProps) => {

    return (

        <div className="book-card">

            <img src={loan.bookDTO.img} alt="cover" width={200} height={320} className="shadow-xl" />

            <div className="flex flex-col gap-10 max-lg:gap-5 xl:w-5/12 lg:flex-1 w-full">

                <div className="max-lg:text-center">

                    <p className="font-semibold lg:text-2xl max-lg:text-xl">{loan.bookDTO.title}</p>
                    <p className="font-light lg:text-xl max-lg:text-lg">{loan.bookDTO.author}</p>

                </div>

                <BookGenres genres={loan.bookDTO.genres} />

            </div>

            <BookLoanInfoBox loan={loan} />

        </div>

    )

}
