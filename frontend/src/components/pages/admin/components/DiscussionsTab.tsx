import { useState } from "react";
import { useAuthenticationContext } from "../../../../authentication/authenticationContext";
import { DiscussionModel } from "../../../../models/DiscussionModel";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { Pagination } from "../../../commons/pagination/Pagination";
import { useFetchOpenDiscussions } from "../../../../utils/api_fetchers/admin_controller/useFetchOpenDiscussions";
import { DiscussionsTabDiscussionCard } from "./DiscussionsTabDiscussionCard";
import { PaginatedItemsCount } from "../../../commons/pagination/PaginatedItemsCount";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

export const DiscussionsTab = () => {

    const { authentication } = useAuthenticationContext();

    const [discussions, setDiscussions] = useState<DiscussionModel[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [httpError, setHttpError] = useState<string | null>(null);
    const [isDiscussionClosed, setIsDiscussionClosed] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const [totalAmountOfDiscussions, setTotalAmountOfDiscussions] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [resultRange, setResultRange] = useState({start: 1, end: 5});

    useFetchOpenDiscussions(authentication, setDiscussions, setTotalAmountOfDiscussions, setTotalPages, setIsLoading, setHttpError, currentPage, isDiscussionClosed);

    const renderContent = () => {
        if (isLoading) {
            return <LoadingSpinner />;
        }

        if (httpError) {
            return <HttpErrorMessage httpError={httpError} />;
        }

        if (discussions.length === 0) {
            return (
                <div className="text-center py-12 text-gray-600">
                    <p className="text-lg">No open discussions at the moment</p>
                    <p className="text-sm mt-2">All discussions have been resolved</p>
                </div>
            );
        }

        return (
            <>
                <PaginatedItemsCount
                    itemsName={"Open discussions"}
                    totalAmountOfItems={totalAmountOfDiscussions}
                    resultRange={resultRange}
                />

                <div className="flex flex-col gap-4">
                    {discussions.map(discussion => (
                        <DiscussionsTabDiscussionCard
                            key={discussion.id}
                            discussion={discussion}
                            setIsDiscussionClosed={setIsDiscussionClosed}
                        />
                    ))}
                </div>

                <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    totalAmountOfItems={totalAmountOfDiscussions}
                    setCurrentPage={setCurrentPage}
                    setResultRange={setResultRange}
                />
            </>
        );
    };

    return (

        <div className="card">

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Open Discussions</h2>

            {renderContent()}

        </div>

    )

}
