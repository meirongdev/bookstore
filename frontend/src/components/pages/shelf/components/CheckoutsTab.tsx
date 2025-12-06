import { useState } from "react";
import { useAuthenticationContext } from "../../../../authentication/authenticationContext";
import { CheckoutModel } from "../../../../models/CheckoutModel";
import { useFetchCurrentCheckouts } from "../../../../utils/api_fetchers/checkout_controller/useFetchCurrentCheckouts";
import { Link } from "react-router-dom";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { CheckoutsTabBookCard } from "./CheckoutsTabBookCard";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

export const CheckoutsTab = () => {

    const { authentication } = useAuthenticationContext();

    const [currentCheckouts, setCurrentCheckouts] = useState<CheckoutModel[]>([]);
    const [isLoadingCheckouts, setIsLoadingCheckouts] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);

    const [isBookReturned, setIsBookReturned] = useState(false);
    const [isCheckoutRenewed, setIsCheckoutRenewed] = useState(false);

    useFetchCurrentCheckouts(authentication, setCurrentCheckouts, setIsLoadingCheckouts, setHttpError, isBookReturned, isCheckoutRenewed);

    const renderContent = () => {
        if (isLoadingCheckouts) {
            return <LoadingSpinner />;
        }

        if (httpError) {
            return <HttpErrorMessage httpError={httpError} />;
        }

        if (currentCheckouts.length === 0) {
            return (
                <div className="text-center py-12">
                    <p className="text-xl text-gray-700 mb-4">You don't have any books checked out.</p>
                    <p className="text-gray-600 mb-6">Start exploring our collection!</p>
                    <Link to={'/search'} className="btn-primary inline-block px-8">
                        Browse Books
                    </Link>
                </div>
            );
        }

        return (
            <div className="flex flex-col gap-4">
                {currentCheckouts.map(checkout => (
                    <CheckoutsTabBookCard
                        key={checkout.bookDTO.id}
                        checkout={checkout}
                        setIsBookReturned={setIsBookReturned}
                        setIsCheckoutRenewed={setIsCheckoutRenewed}
                    />
                ))}
            </div>
        );
    };

    return (

        <div className="card">

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Current Checkouts</h2>

            {renderContent()}

        </div>

    )

}
