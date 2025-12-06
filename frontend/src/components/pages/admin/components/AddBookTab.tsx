import { useState } from "react";
import { useAuthenticationContext } from "../../../../authentication/authenticationContext";
import { BookModel } from "../../../../models/BookModel";
import { FormLoader } from "../../../commons/form_loader/FormLoader";
import { FieldErrors } from "../../../commons/field_errors/FieldErrors";
import { GenreModel } from "../../../../models/GenreModel";
import { addNewBook } from "../../../../utils/api_fetchers/admin_controller/useAddNewBook";
import { useFetchAllGenres } from "../../../../utils/api_fetchers/genre_controller/useFetchAllGenres";
import { LoadingSpinner } from "../../../commons/loading_spinner/LoadingSpinner";
import { HttpErrorMessage } from "../../../commons/http_error_message/HttpErrorMessage";
import { useUploadImage } from "../../../../utils/api_fetchers/upload_controller/useUploadImage";

export const AddBookTab = () => {

    const { authentication } = useAuthenticationContext();
    const { uploadImage } = useUploadImage();

    const [newBook, setNewBook] = useState<BookModel>({ title: "", author: "", description: "", copies: 0, copiesAvailable: 0, genres: [], img: "" });
    const [selectedImage, setSelectedImage] = useState<File | null>(null);
    const [isLoadingSubmit, setIsLoadingSubmit] = useState(false);
    const [bookSubmitHttpError, setBookSubmitHttpError] = useState<string | null>(null);
    const [displaySuccess, setDisplaySuccess] = useState(false);

    const [allGenres, setAllGenres] = useState<GenreModel[]>([]);
    const [isLoadingGenres, setIsLoadingGenres] = useState(true);
    const [genresHttpError, setGenresHttpError] = useState<string | null>(null);

    useFetchAllGenres(setAllGenres, setIsLoadingGenres, setGenresHttpError);

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            setSelectedImage(file);
        }
    };

    const handleChange = (event: React.ChangeEvent<HTMLInputElement> | React.ChangeEvent<HTMLTextAreaElement>) => {

        setNewBook({ ...newBook, [event.target.name]: event.target.value });
    };

    const handleGenreClick = (genre: GenreModel) => {

        if (newBook.genres.length === 0) {
            setNewBook({ ...newBook, genres: [genre] });
        } else if (newBook.genres.some(g => g.id === genre.id)) {
            setNewBook({ ...newBook, genres: newBook.genres.filter(item => item.id !== genre.id) });
        } else {
            setNewBook({ ...newBook, genres: [...newBook.genres, genre] });
        }
    }

    const handleSubmitBookClick = async () => {
        setIsLoadingSubmit(true);
        setBookSubmitHttpError(null);
        setDisplaySuccess(false);

        if (!selectedImage) {
            setBookSubmitHttpError("Please select an image.");
            setIsLoadingSubmit(false);
            return;
        }

        if (newBook.genres.length === 0) {
            setBookSubmitHttpError("Please select at least one genre.");
            setIsLoadingSubmit(false);
            return;
        }

        try {
            const imageUrl = await uploadImage(selectedImage);
            const bookToSubmit = { ...newBook, img: imageUrl };
            await addNewBook(authentication, bookToSubmit, setNewBook, setIsLoadingSubmit, setBookSubmitHttpError, setDisplaySuccess);
            if (!bookSubmitHttpError) {
                setNewBook({ title: "", author: "", description: "", copies: 0, copiesAvailable: 0, genres: [], img: "" });
                setSelectedImage(null);
            }
        } catch (error: any) {
            setBookSubmitHttpError(error.message);
        } finally {
            setIsLoadingSubmit(false);
        }
    };

    return (

        <div className="card">

            {displaySuccess &&

                <div className="flex items-center gap-2 text-green-700 bg-green-50 border border-green-200 rounded-lg px-4 py-3">
                    <span className="text-2xl">✓</span>
                    <span className="font-medium">New book added successfully!</span>
                </div>

            }

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Add New Book</h2>

            <FormLoader isLoading={isLoadingSubmit} />

            {(bookSubmitHttpError && !bookSubmitHttpError.startsWith("Some")) && <HttpErrorMessage httpError={bookSubmitHttpError} />}

            <form className="flex flex-col gap-5 w-full">

                <div className="grid grid-cols-1 lg:grid-cols-12 gap-4">

                    <div className="lg:col-span-7">
                        <FieldErrors fieldName="title" httpError={bookSubmitHttpError} />
                        <input
                            type="text"
                            name="title"
                            value={newBook.title}
                            onChange={handleChange}
                            placeholder="Book title"
                            className="input"
                            required
                        />
                    </div>

                    <div className="lg:col-span-5">
                        <FieldErrors fieldName="author" httpError={bookSubmitHttpError} />
                        <input
                            type="text"
                            name="author"
                            value={newBook.author}
                            onChange={handleChange}
                            placeholder="Author"
                            className="input"
                            required
                        />
                    </div>

                </div>

                <div>
                    <FieldErrors fieldName="description" httpError={bookSubmitHttpError} />
                    <textarea
                        rows={3}
                        name="description"
                        value={newBook.description}
                        onChange={handleChange}
                        placeholder="Book description"
                        className="input"
                        required
                    />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">

                    <div>
                        <FieldErrors fieldName="copies" httpError={bookSubmitHttpError} />
                        <span className="block text-sm font-medium text-gray-700 mb-1">
                            Total Copies
                        </span>
                        <input
                            type="number"
                            name="copies"
                            value={newBook.copies}
                            onChange={handleChange}
                            className="input"
                            min="0"
                            required
                        />
                    </div>

                    <div>
                        <FieldErrors fieldName="copiesAvailable" httpError={bookSubmitHttpError} />
                        <span className="block text-sm font-medium text-gray-700 mb-1">
                            Copies Available
                        </span>
                        <input
                            type="number"
                            name="copiesAvailable"
                            value={newBook.copiesAvailable}
                            onChange={handleChange}
                            className="input"
                            min="0"
                            required
                        />
                    </div>

                    <div >
                        <FieldErrors fieldName="img" httpError={bookSubmitHttpError} />
                        <span className="block text-sm font-medium text-gray-700 mb-1">
                            Book Cover Image
                        </span>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleImageChange}
                            id="fileInput"
                            className="input file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-violet-50 file:text-violet-700 hover:file:bg-violet-100"
                            required
                        />
                    </div>

                </div>

                <div>
                    <FieldErrors fieldName="genres" httpError={bookSubmitHttpError} />

                    <div className="border-2 border-gray-200 rounded-lg p-5 bg-gray-50">

                        <p className="text-sm font-medium text-gray-700 mb-3">Select Genres:</p>

                        {isLoadingGenres && <LoadingSpinner />}

                        {!isLoadingGenres && genresHttpError && <HttpErrorMessage httpError={genresHttpError} />}

                        {!isLoadingGenres && !genresHttpError && (
                            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                                {allGenres.map(genre => (
                                    <label
                                        key={genre.id}
                                        className="flex items-center gap-2 p-3 rounded-md border-2 border-gray-300 bg-white hover:border-gray-800 cursor-pointer transition-colors"
                                    >
                                        <input
                                            type="checkbox"
                                            className="checkbox"
                                            checked={newBook.genres.some(g => g.id === genre.id)}
                                            onChange={() => handleGenreClick(genre)}
                                        />
                                        <span className="text-sm">{genre.description}</span>
                                    </label>
                                ))}
                            </div>
                        )}

                    </div>

                </div>

            </form>

            <button className="btn-primary w-full" onClick={handleSubmitBookClick}>
                Add Book
            </button>

        </div>

    )

}
