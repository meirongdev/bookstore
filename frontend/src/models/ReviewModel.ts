export class ReviewModel {

    userEmail: string;
    userFirstName: string;
    date: Date;
    rating: number;
    reviewDescription: string;
    id?: number;

    constructor ( userEmail: string, userFirstName: string, date: Date, rating: number, reviewDescription: string, id?: number) {

        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.date = date;
        this.rating = rating;
        this.reviewDescription = reviewDescription;
        this.id = id;
    }

}
