package dev.meirong.showcase.bookstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.ReviewDTO;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import dev.meirong.showcase.bookstore.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Controller")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @Operation(summary = "Get paginated list of books.", description = "Returns a Page containing BookDTO objects.")
  @GetMapping
  public ResponseEntity<Page<BookDTO>> findAll(
      @RequestParam() Integer page,
      @RequestParam(value = "books-per-page") Integer booksPerPage) {

    Page<BookDTO> responseBody = bookService.findAll(PageRequest.of(page, booksPerPage));
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Get book by it's ID.", description = "Returns a JSON value of type BookDTO.")
  @GetMapping("/{bookId}")
  public ResponseEntity<BookDTO> findById(@PathVariable() Long bookId) {

    BookDTO responseBody = bookService.findById(bookId);
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Get paginated list of books, found by title.", description = "Returns a Page containing BookDTO objects.")
  @GetMapping("/search/by-title")
  public ResponseEntity<Page<BookDTO>> findAllByTitle(
      @RequestParam() Integer page,
      @RequestParam(value = "books-per-page") Integer booksPerPage,
      @RequestParam("title-query") String titleQuery) {

    Page<BookDTO> responseBody = bookService.findAllByTitle(titleQuery, PageRequest.of(page, booksPerPage));
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Get paginated list of books, found by genre.", description = "Returns a Page containing BookDTO objects.")
  @GetMapping("/search/by-genre")
  public ResponseEntity<Page<BookDTO>> findAllByGenre(
      @RequestParam("genre-query") String genreQuery,
      @RequestParam() Integer page,
      @RequestParam(value = "books-per-page") Integer booksPerPage) {

    Page<BookDTO> responseBody = bookService.findAllByGenre(genreQuery, PageRequest.of(page, booksPerPage));
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Add a new book.", description = "Returns the newly created BookDTO.")
  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping("/secure/add")
  public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
    BookDTO responseBody = bookService.addBook(bookDTO);
    return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
  }

  @Operation(summary = "Check if the book is checked out by authenticated user.", description = "Returns a Boolean value.")
  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping("/secure/is-checked-out/{bookId}")
  public ResponseEntity<Boolean> isBookCheckedOutByUser(
      @PathVariable("bookId") Long bookId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Boolean responseBody = bookService.isBookCheckedOutByUser(userDetails.getUsername(), bookId);
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Check out the book.", description = "Creates new Checkout Entity and reduces book's copies available amount.")
  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping("/secure/checkout/{bookId}")
  public ResponseEntity<Void> checkoutBook(
      @PathVariable() Long bookId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    bookService.checkoutBook(userDetails.getUsername(), bookId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Renew checkout for the book.", description = "Updates related Checkout Entity.")
  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping("/secure/renew-checkout/{bookId}")
  public ResponseEntity<Void> renewCheckout(
      @PathVariable("bookId") Long bookId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    bookService.renewCheckout(userDetails.getUsername(), bookId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Return the book back to store.", description = "Updates user's payment amount if the book is outdated. Deletes related Checkout Entity. Creates new History Record Entity. Updates book's copies available amount")
  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping("/secure/return/{bookId}")
  public ResponseEntity<Void> returnBook(
      @PathVariable() Long bookId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    bookService.returnBook(userDetails.getUsername(), bookId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Check if the book is reviewed by authenticated user.", description = "Returns a Boolean value.")
  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping("/secure/is-reviewed/{bookId}")
  public ResponseEntity<Boolean> isBookReviewedByUser(
      @PathVariable("bookId") Long bookId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Boolean responseBody = bookService.isBookReviewedByUser(userDetails.getUsername(), bookId);
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Review the book.", description = "Creates new Review Entity.")
  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping("/secure/review/{bookId}")
  public ResponseEntity<ReviewDTO> reviewBook(
      @PathVariable("bookId") Long bookId,
      @Valid @RequestBody ReviewDTO reviewDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    ReviewDTO responseBody = bookService.reviewBook(userDetails.getUsername(), bookId, reviewDTO);
    return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
  }
}
