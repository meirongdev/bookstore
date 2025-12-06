package dev.meirong.showcase.bookstore.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.GenreDTO;
import dev.meirong.showcase.bookstore.services.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genre Controller")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @Operation(summary = "Get the list of all Genres.",
            description = "Returns a List of GenreDTO objects.")
    @GetMapping
    public ResponseEntity<List<GenreDTO>> findAll() {

        List<GenreDTO> responseBody = genreService.findAll();
        return ResponseEntity.ok(responseBody);
    }
}
