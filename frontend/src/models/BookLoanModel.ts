import { BookModel } from "./BookModel";

/**
 * BookLoanModel represents a historical book borrowing record.
 * Contains information about when a book was borrowed and returned by a user.
 */
export class BookLoanModel {

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