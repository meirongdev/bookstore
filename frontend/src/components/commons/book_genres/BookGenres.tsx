import { GenreModel } from "../../../models/GenreModel"

type BookGenresProps = {
    genres: GenreModel[]
}

export const BookGenres = ({ genres }: BookGenresProps) => {
    return (
        <div className="flex flex-wrap gap-2">
            {genres.map((genre) => (
                <span key={genre.description} className="tag">
                    {genre.description}
                </span>
            ))}
        </div>
    )
}
