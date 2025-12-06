package dev.meirong.showcase.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for BookLoan entity representing a user's book borrowing history record.
 * Contains information about when a book was borrowed and returned.
 */
@Getter
@Setter
public class BookLoanDTO {

    private Long id;

    private BookDTO bookDTO;

    private LocalDate borrowedDate;

    private LocalDate returnedDate;

    private String status;

    private Long createdAt;

    private Long updatedAt;
}
