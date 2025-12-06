package dev.meirong.showcase.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.meirong.showcase.bookstore.entities.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByDescription(String description);

    List<Genre> findByDescriptionIn(List<String> descriptions);
}
