import { BookModel } from "./BookModel";

/**
 * BookLoanHistoryModel represents a historical book borrowing record.
 * Contains information about when a book was borrowed and returned by a user.
 */
export class BookLoanHistoryModel {

    id: number;
    bookDTO: BookModel;
    borrowedDate: Date;
    returnedDate: Date;
    status?: string;
    createdAt?: number;
    updatedAt?: number;

    constructor(
        id: number,
        bookDTO: BookModel,
        borrowedDate: Date,
        returnedDate: Date,
        status?: string,
        createdAt?: number,
        updatedAt?: number
    ) {
        this.id = id;
        this.bookDTO = bookDTO;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
