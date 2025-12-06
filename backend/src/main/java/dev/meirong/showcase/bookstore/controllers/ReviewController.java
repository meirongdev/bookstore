package dev.meirong.showcase.bookstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.ReviewDTO;
import dev.meirong.showcase.bookstore.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @Operation(summary = "Get a paginated list of reviews for a specific book.", description = "Returns a page containing ReviewDTO objects.")
  @GetMapping("/{bookId}")
  public ResponseEntity<Page<ReviewDTO>> findAllByBookId(
      @PathVariable Long bookId,
      @RequestParam Integer page,
      @RequestParam("reviews-per-page") Integer reviewsPerPage,
      @RequestParam(defaultValue = "false") boolean latest) {

    Page<ReviewDTO> responseBody = reviewService.findAllByBookId(bookId, PageRequest.of(page, reviewsPerPage), latest);
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Get an average rating for a specific book.", description = "Counts an average rating across all the reviews related to selected book. Returns a value of type Double.")
  @GetMapping("/average-rating/{bookId}")
  public ResponseEntity<Double> getAverageRatingByBookId(@PathVariable("bookId") Long bookId) {

    Double responseBody = reviewService.getAverageRatingByBookId(bookId);
    return ResponseEntity.ok(responseBody);
  }
}
