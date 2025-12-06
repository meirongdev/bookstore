package dev.meirong.showcase.bookstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.DiscussionDTO;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import dev.meirong.showcase.bookstore.services.BookService;
import dev.meirong.showcase.bookstore.services.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/secure")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin Controller")
@RequiredArgsConstructor
public class AdminController {

  private final BookService bookService;
  private final DiscussionService discussionService;

  @Operation(summary = "Add new book to DataBase.", description = "Requires a BookDTO object as a request body.")
  @PostMapping("/add-book")
  public ResponseEntity<BookDTO> postBook(
      @RequestBody @Valid BookDTO bookDTO) {

    BookDTO responseBody = bookService.addBook(bookDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
  }

  @Operation(summary = "Increase quantity of a specific book by 1.", description = "Changes copies and copies available fields of a selected book.")
  @PatchMapping("/increase-quantity/{bookId}")
  public ResponseEntity<Void> increaseBookQuantity(@PathVariable("bookId") Long bookId) {

    bookService.changeQuantity(bookId, "increase");
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Decrease quantity of a specific book by 1.", description = "Changes copies and copies available fields of a selected book.")
  @PatchMapping("/decrease-quantity/{bookId}")
  public ResponseEntity<Void> decreaseBookQuantity(@PathVariable("bookId") Long bookId) {

    bookService.changeQuantity(bookId, "decrease");
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete a book from a DataBase.", description = "Permanently deletes a book entity from a DataBase.")
  @DeleteMapping("/delete-book/{bookId}")
  public ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId) {

    bookService.deleteById(bookId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get a paginated list of all open discussions.", description = "Returns a Page containing DiscussionDTO objects.")
  @GetMapping("/open-discussions")
  public ResponseEntity<Page<DiscussionDTO>> findAllUnclosedDiscussions(
      @RequestParam(value = "page") Integer page,
      @RequestParam(value = "discussions-per-page") Integer discussionsPerPage) {

    Page<DiscussionDTO> responseBody = discussionService.findAllByClosed(PageRequest.of(page, discussionsPerPage));
    return ResponseEntity.ok(responseBody);
  }

  @Operation(summary = "Update specific discussion entity.", description = "Sets the administration answer to a selected discussion entity and marks it as closed. Requires a valid DiscussionDTO object as a request body.")
  @PatchMapping("/close-discussion")
  public ResponseEntity<Void> updateDiscussion(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid DiscussionDTO discussionDTO,
      BindingResult bindingResult) {

    discussionService.updateDiscussion(userDetails.getUsername(), discussionDTO, bindingResult);
    return ResponseEntity.noContent().build();
  }
}
