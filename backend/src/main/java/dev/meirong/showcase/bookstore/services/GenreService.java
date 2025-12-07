package dev.meirong.showcase.bookstore.services;

import org.springframework.stereotype.Service;

import dev.meirong.showcase.bookstore.dto.GenreDTO;
import dev.meirong.showcase.bookstore.entities.Genre;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.GenreRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final EntityMapper entityMapper;
    private final GenreRepository genreRepository;


    public List<GenreDTO> findAll() {

        return genreRepository.findAll().stream().map(this::convertToGenreDTO).toList();
    }

    private GenreDTO convertToGenreDTO(Genre genre) {
        return entityMapper.toGenreDTO(genre);
    }
}
