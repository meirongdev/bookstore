import { useState } from "react";
import { useFetchAllGenres } from "../../../../utils/api_fetchers/genre_controller/useFetchAllGenres";
import { GenreModel } from "../../../../models/GenreModel";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";

type SearchPanelProps = {
    selectedGenre: string,
    handleGenreChange: (value: string) => void,
    titleQuery: string,
    setTitleQuery: React.Dispatch<React.SetStateAction<string>>,
    handleSearchClick: () => void
}

export const SearchPanel = ({ selectedGenre, handleGenreChange, titleQuery, setTitleQuery, handleSearchClick }: SearchPanelProps) => {

    const [allGenres, setAllGenres] = useState<GenreModel[]>([]);
    const [isLoadingGenres, setIsLoadingGenres] = useState(true);
    const [genresHttpError, setGenresHttpError] = useState<string | null>(null);

    useFetchAllGenres(setAllGenres, setIsLoadingGenres, setGenresHttpError);

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearchClick();
        }
    };

    return (

        <div className="card">

            <div className="grid grid-cols-1 sm:grid-cols-12 gap-3">

                {isLoadingGenres ? (
                    <div className="sm:col-span-3">
                        <LoadingSpinner />
                    </div>
                ) : genresHttpError ? (
                    <div className="sm:col-span-12">
                        <HttpErrorMessage httpError={genresHttpError} />
                    </div>
                ) : (
                    <select
                        className="dropdown sm:col-span-3"
                        value={selectedGenre}
                        onChange={event => handleGenreChange(event.target.value)}
                    >
                        <option value="">All Genres</option>
                        {allGenres.map(genre => (
                            <option key={genre.id} value={genre.description}>
                                {genre.description}
                            </option>
                        ))}
                    </select>
                )}

                <input
                    type="text"
                    className="input sm:col-span-7"
                    placeholder="Search by title..."
                    value={titleQuery}
                    onChange={event => setTitleQuery(event.target.value)}
                    onKeyDown={handleKeyDown}
                />

                <button
                    className="btn-primary sm:col-span-2"
                    onClick={handleSearchClick}
                >
                    Search
                </button>

            </div>

        </div>

    )

}
