package dev.meirong.showcase.bookstore.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.BookLoanDTO;
import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.BookLoan;
import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.BookLoanRepository;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user's book borrowing history.
 * Provides operations to retrieve and display historical loan records.
 */
@Service
@RequiredArgsConstructor
public class BookLoanService {

    private final EntityMapper entityMapper;
    private final BookLoanRepository bookLoanRepository;
    private final UserRepository userRepository;

    /**
     * Find all book loans (borrowing history) for a specific user
     * @param userEmail The user's email
     * @param pageable Pagination information
     * @return Page of BookLoanDTO containing user's borrowing history
     */
    public Page<BookLoanDTO> findAllByUserEmail(String userEmail, Pageable pageable) {

        User user = getUserFromRepository(userEmail);

        Page<BookLoan> bookLoans = bookLoanRepository.findByBorrower(user, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        List<BookLoanDTO> pageContent = new ArrayList<>();

        for (BookLoan bookLoan : bookLoans) {
            BookLoanDTO bookLoanDTO = convertToBookLoanDTO(bookLoan);
            bookLoanDTO.setBookDTO(convertToBookDTO(bookLoan.getBook()));
            pageContent.add(bookLoanDTO);
        }

        return new PageImpl<>(pageContent, bookLoans.getPageable(), bookLoans.getTotalElements());
    }

    private User getUserFromRepository(String userEmail) {

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            ErrorsUtil.returnUserError("User with such email is not found.", null, HttpStatus.NOT_FOUND);
            return null; // or throw an exception if ErrorsUtil does not throw
        }

        return user.get();
    }

    private BookLoanDTO convertToBookLoanDTO(BookLoan bookLoan) {
        return entityMapper.toBookLoanDTO(bookLoan);
    }

    private BookDTO convertToBookDTO(Book book) {
        return entityMapper.toBookDTO(book);
    }
}
