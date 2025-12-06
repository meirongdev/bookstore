package dev.meirong.showcase.bookstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.meirong.showcase.bookstore.entities.BookLoan;
import dev.meirong.showcase.bookstore.entities.User;

/**
 * Repository for managing BookLoan (historical book borrowing records).
 * Provides methods to query user's borrowing history.
 */
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    /**
     * Find all book loans for a specific user (borrowing history)
     */
    Page<BookLoan> findByBorrower(User user, Pageable pageable);
}
