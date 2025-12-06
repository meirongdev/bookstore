import { useState } from "react";
import { DiscussionModel } from "../../../../models/DiscussionModel";
import { useAuthenticationContext } from "../../../../authentication/authenticationContext";
import { useFetchDiscussions } from "../../../../utils/api_fetchers/discussion_controller/useFetchDiscussions";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { Pagination } from "../../../commons/pagination/Pagination";
import { AllDiscussionsTabDiscussionCard } from "./AllDiscussionsTabDiscussionCard";
import { PaginatedItemsCount } from "../../../commons/pagination/PaginatedItemsCount";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

type AllDiscussionsTabProps = {
    setActiveTab: React.Dispatch<React.SetStateAction<'new' | 'all'>>
}

export const AllDiscussionsTab = ({ setActiveTab }: AllDiscussionsTabProps) => {

    const { authentication } = useAuthenticationContext();

    const [discussions, setDiscussions] = useState<DiscussionModel[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [httpError, setHttpError] = useState<string | null>(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [totalAmountOfDiscussions, setTotalAmountOfDiscussions] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [resultRange, setResultRange] = useState({start: 1, end: 5});

    useFetchDiscussions(authentication, setDiscussions, setTotalAmountOfDiscussions, setTotalPages, setIsLoading, setHttpError, currentPage);

    const renderContent = () => {
        if (isLoading) {
            return <LoadingSpinner />;
        }

        if (httpError) {
            return <HttpErrorMessage httpError={httpError} />;
        }

        if (discussions.length === 0) {
            return (
                <div className="text-center py-12">
                    <p className="text-xl text-gray-700 mb-4">You haven't started any discussions yet.</p>
                    <p className="text-gray-600 mb-6">Have a question? Start a new discussion!</p>
                    <button className="btn-primary inline-block px-8" onClick={() => setActiveTab('new')}>
                        Start Discussion
                    </button>
                </div>
            );
        }

        return (
            <>
                <PaginatedItemsCount
                    itemsName={"Your discussions"}
                    totalAmountOfItems={totalAmountOfDiscussions}
                    resultRange={resultRange}
                />

                <div className="flex flex-col gap-4">
                    {discussions.map(discussion => (
                        <AllDiscussionsTabDiscussionCard key={discussion.id} discussion={discussion} />
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

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Your Discussions</h2>

            {renderContent()}

        </div>

    )

}
