package dev.meirong.showcase.bookstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.BookLoanDTO;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import dev.meirong.showcase.bookstore.services.BookLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing user's book borrowing history.
 * Provides endpoints to retrieve historical loan records.
 */
@RestController
@RequestMapping("/api/book-loans/secure")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Book Loan History Controller")
@RequiredArgsConstructor
public class BookLoanController {

  private final BookLoanService bookLoanService;

  @Operation(summary = "Get user's book borrowing history", description = "Returns a paginated list of BookLoanDTO objects containing the user's historical book borrowing and return records.")
  @GetMapping
  public ResponseEntity<Page<BookLoanDTO>> findAllByUserEmail(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam Integer page,
      @RequestParam(value = "records-per-page") Integer recordsPerPage) {

    Page<BookLoanDTO> responseBody = bookLoanService.findAllByUserEmail(
        userDetails.getUsername(),
        PageRequest.of(page, recordsPerPage));
    return ResponseEntity.ok(responseBody);
  }
}
