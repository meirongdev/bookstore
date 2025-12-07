package dev.meirong.showcase.bookstore.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.GenreDTO;
import dev.meirong.showcase.bookstore.dto.ReviewDTO;
import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.Checkout;
import dev.meirong.showcase.bookstore.entities.Genre;
import dev.meirong.showcase.bookstore.entities.BookLoan;
import dev.meirong.showcase.bookstore.entities.Payment;
import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.entities.Review;
import dev.meirong.showcase.bookstore.enums.PaymentStatus;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.BookRepository;
import dev.meirong.showcase.bookstore.repositories.CheckoutRepository;
import dev.meirong.showcase.bookstore.repositories.GenreRepository;
import dev.meirong.showcase.bookstore.repositories.BookLoanRepository;
import dev.meirong.showcase.bookstore.repositories.PaymentRepository;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.repositories.ReviewRepository;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final EntityMapper entityMapper;
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final CheckoutRepository checkoutRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final BookLoanRepository bookLoanRepository;

    public Page<BookDTO> findAll(Pageable pageable) {

        Page<Book> page = bookRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        return page.map(this::convertToBookDTO);
    }

    public BookDTO findById(Long bookId) {

        Book book = getBookFromRepository(bookId);

        return convertToBookDTO(book);
    }

    public Page<BookDTO> findAllByTitle(String titleQuery, Pageable pageable) {

        return bookRepository.findByTitleContainingIgnoreCase(titleQuery, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
                .map(this::convertToBookDTO);
    }

    public Page<BookDTO> findAllByGenre(String genreQuery, Pageable pageable) {

        Optional<Genre> genre = genreRepository.findByDescription(genreQuery);

        if (!genre.isPresent()) {
            ErrorsUtil.returnGenreError("No such genre found", null, HttpStatus.NOT_FOUND);
            return Page.empty(pageable); // Ensure method returns if genre not found
        }

        return bookRepository.findByGenresContains(genre.get(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
                .map(this::convertToBookDTO);
    }

    @Transactional
    public BookDTO addBook(BookDTO bookDTO) {

        Book book = convertToBook(bookDTO);

        List<String> genreDescriptions = bookDTO.getGenres().stream().map(GenreDTO::getDescription).toList();
        List<Genre> genres = genreRepository.findByDescriptionIn(genreDescriptions);

        if (genres.size() != genreDescriptions.size()) {
            ErrorsUtil.returnGenreError("No such genres found", null, HttpStatus.NOT_FOUND);
        }

        book.setGenres(genres);
        genres.forEach(genre -> genre.getBooks().add(book));
        Book savedBook = bookRepository.save(book);

        return convertToBookDTO(savedBook);
    }

    @Transactional
    public void deleteById(Long bookId) {

        bookRepository.deleteById(bookId);
    }

    @Transactional
    public void changeQuantity(Long bookId, String operation) {

        Book book = getBookFromRepository(bookId);

        if (operation.equals("increase") && book != null) {
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            book.setCopies(book.getCopies() + 1);
            bookRepository.save(book);
        }

        if (operation.equals("decrease")) {

            if (book == null || book.getCopiesAvailable() <= 0 || book.getCopies() <= 0) {
                ErrorsUtil.returnBookError("Book quantity is already 0", null, HttpStatus.FORBIDDEN);
            }

            if (book != null) {
                book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                book.setCopies(book.getCopies() - 1);
                bookRepository.save(book);
            }
        }
    }

    public boolean isBookCheckedOutByUser(String userEmail, Long bookId) {

        User user = getUserFromRepository(userEmail);
        Book book = getBookFromRepository(bookId);
        Optional<Checkout> checkout = getCheckoutOptionalFromRepository(user, book);

        return checkout.isPresent();
    }

    @Transactional
    public void checkoutBook(String userEmail, Long bookId) {

        User user = getUserFromRepository(userEmail);
        Book book = getBookFromRepository(bookId);
        Optional<Checkout> checkout = getCheckoutOptionalFromRepository(user, book);

        if (book == null || book.getCopiesAvailable() <= 0 || book.getCopies() <= 0) {
            ErrorsUtil.returnBookError("Book quantity is already 0", null, HttpStatus.FORBIDDEN);
        }

        if (checkout.isPresent()) {
            ErrorsUtil.returnBookError("Book is already checked out by this user", null, HttpStatus.FORBIDDEN);
        }

        List<Checkout> currentCheckouts = checkoutRepository.findByCheckoutHolder(user);
        boolean bookNeedsReturned = false;

        for (Checkout currentCheckout : currentCheckouts) {

            LocalDate d1 = currentCheckout.getReturnDate();
            LocalDate d2 = LocalDate.now();

            if (d2.isAfter(d1)) {
                bookNeedsReturned = true;
                break;
            }
        }

        Optional<Payment> payment = getPaymentOptionalFromRepository(user);

        if (payment.isPresent() && (payment.get().getAmount().compareTo(BigDecimal.ZERO) > 0 || bookNeedsReturned)) {
            ErrorsUtil.returnPaymentError("You have outstanding fees / overdue books, checkout is unavailable", HttpStatus.FORBIDDEN);
        }

        Checkout newCheckout = new Checkout(user, book, LocalDate.now(), LocalDate.now().plusDays(7));
        checkoutRepository.save(newCheckout);

        if (book != null) {
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            book.getCheckouts().add(newCheckout);
            bookRepository.save(book);
        }
    }

    @Transactional
    public void renewCheckout(String userEmail, Long bookId) {

        User user = getUserFromRepository(userEmail);
        Book book = getBookFromRepository(bookId);
        Checkout checkout = getCheckoutOptionalFromRepository(user, book)
                .orElseThrow(() -> {
                    ErrorsUtil.returnBookError("This book is not checked out by this user", null, HttpStatus.FORBIDDEN);
                    return null; // This line is technically unreachable but required by orElseThrow
                });

        LocalDate returnDate = checkout.getReturnDate();
        LocalDate currentDate = LocalDate.now();

        if (returnDate.isAfter(currentDate) || returnDate.isEqual(currentDate)) {
            checkout.setReturnDate(LocalDate.now().plusDays(7));
            checkoutRepository.save(checkout);
        }

        if (returnDate.isBefore(currentDate)) {
            ErrorsUtil.returnBookError("This book is overdue", null, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public void returnBook(String userEmail, Long bookId) {

        User user = getUserFromRepository(userEmail);
        Book book = getBookFromRepository(bookId);
        Checkout checkout = getCheckoutOptionalFromRepository(user, book)
                .orElseThrow(() -> {
                    ErrorsUtil.returnBookError("This book is not checked out by this user", null, HttpStatus.FORBIDDEN);
                    return null; // This line is technically unreachable but required by orElseThrow
                });

        LocalDate returnDate = checkout.getReturnDate();
        LocalDate currentDate = LocalDate.now();

        // Only create payment record if book is overdue
        if (returnDate.isBefore(currentDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(returnDate, currentDate);
            BigDecimal overdueAmount = BigDecimal.valueOf(daysOverdue);

            // Get or create payment record for overdue fees
            Optional<Payment> paymentOptional = getPaymentOptionalFromRepository(user);
            Payment payment;

            if (paymentOptional.isPresent()) {
                payment = paymentOptional.get();
                payment.setAmount(payment.getAmount().add(overdueAmount));
            } else {
                payment = Payment.builder()
                        .paymentHolder(user)
                        .amount(overdueAmount)
                        .currency("usd")
                        .status(PaymentStatus.PENDING)
                        .description("Overdue fees for book rental")
                        .build();
            }

            paymentRepository.save(payment);
        }

        // Create a book loan record for history tracking
        BookLoan bookLoan = new BookLoan(user, book, checkout.getCheckoutDate(), LocalDate.now());
        bookLoanRepository.save(bookLoan);

        if (book != null) {
            book.getCheckouts().remove(checkout);
            book.getBookLoans().add(bookLoan);
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            bookRepository.save(book);
        }

        checkoutRepository.deleteById(checkout.getId());
    }

    public boolean isBookReviewedByUser(String userEmail, Long bookId) {

        Book book = getBookFromRepository(bookId);
        Optional<Review> review = reviewRepository.findByUserEmailAndReviewedBook(userEmail, book);

        return review.isPresent();
    }

    @Transactional
    public ReviewDTO reviewBook(String userEmail, Long bookId, ReviewDTO reviewDTO) {

        Review newReview = convertToReview(reviewDTO);

        User user = getUserFromRepository(userEmail);
        if (user == null) {
            ErrorsUtil.returnUserError("User with such email is not found.", null, HttpStatus.NOT_FOUND);
            return null; // This line is technically unreachable if ErrorsUtil throws an exception
        }

        Book book = getBookFromRepository(bookId);
        Optional<Review> review = reviewRepository.findByUserEmailAndReviewedBook(userEmail, book);

        if (review.isPresent()) {
            ErrorsUtil.returnReviewError("This book is already reviewed by this user", null, HttpStatus.FORBIDDEN);
        }

        newReview.setUserEmail(userEmail);
        newReview.setUserFirstName(user.getFirstName());
        newReview.setUserLastName(user.getLastName());
        newReview.setReviewedBook(book);

        Review savedReview = reviewRepository.save(newReview);

        return convertToReviewDTO(savedReview);
    }

//  <-------------------------------------------------------------------------------------------->
//  <-------------------- Service private methods for some code re-usability -------------------->
//  <-------------------------------------------------------------------------------------------->

    private Book getBookFromRepository(Long bookId) {

        Optional<Book> book = bookRepository.findById(bookId);

        if (book.isPresent()) {
            return book.get();
        } else {
            ErrorsUtil.returnBookError("Book not found", null, HttpStatus.NOT_FOUND);
            return null; // This line is technically unreachable if ErrorsUtil throws an exception
        }
    }

    private User getUserFromRepository(String userEmail) {

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isPresent()) {
            return user.get();
        } else {
            ErrorsUtil.returnUserError("User with such email is not found.", null, HttpStatus.NOT_FOUND);
            return null; // This line is technically unreachable if ErrorsUtil throws an exception
        }
    }

    private Optional<Checkout> getCheckoutOptionalFromRepository(User user, Book book) {

        return checkoutRepository.findByCheckoutHolderAndCheckedOutBook(user, book);
    }

    private Optional<Payment> getPaymentOptionalFromRepository(User user) {

        return paymentRepository.findByPaymentHolder(user);
    }

    private Book convertToBook(BookDTO bookDTO) {
        return entityMapper.toBook(bookDTO);
    }

    private BookDTO convertToBookDTO(Book book) {
        return entityMapper.toBookDTO(book);
    }

    private Review convertToReview(ReviewDTO reviewDTO) {
        return entityMapper.toReview(reviewDTO);
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        return entityMapper.toReviewDTO(review);
    }
}
