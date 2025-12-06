package dev.meirong.showcase.bookstore.exceptions;

import com.stripe.exception.StripeException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("One or more fields have an invalid value.");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errors", errors); // Add field errors as a custom property

        return problemDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Your authentication token is expired, please re-login."
        );
        problemDetail.setTitle("JWT Token Expired");
        return problemDetail;
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Your authentication token is invalid or its signature cannot be trusted, please re-login."
        );
        problemDetail.setTitle("JWT Signature Invalid");
        return problemDetail;
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail handleMalformedJwtException(MalformedJwtException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Your authentication token is invalid or malformed, please re-login."
        );
        problemDetail.setTitle("JWT Malformed");
        return problemDetail;
    }

    @ExceptionHandler(BookException.class)
    public ProblemDetail handleBookException(BookException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );
        problemDetail.setTitle("Book Error");
        return problemDetail;
    }

    @ExceptionHandler(DiscussionException.class)
    public ProblemDetail handleDiscussionException(DiscussionException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );
        problemDetail.setTitle("Discussion Error");
        return problemDetail;
    }

    @ExceptionHandler(GenreException.class)
    public ProblemDetail handleGenreException(GenreException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );
        problemDetail.setTitle("Genre Error");
        return problemDetail;
    }

    @ExceptionHandler(PaymentException.class)
    public ProblemDetail handlePaymentException(PaymentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );
        problemDetail.setTitle("Payment Error");
        return problemDetail;
    }

    @ExceptionHandler(UserException.class)
    public ProblemDetail handleUserException(UserException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );
        problemDetail.setTitle("User Error");
        return problemDetail;
    }

    @ExceptionHandler(ReviewException.class)
    public ProblemDetail handleReviewException(ReviewException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getHttpStatus(),
                e.getMessage()
        );
        problemDetail.setTitle("Review Error");
        return problemDetail;
    }

    @ExceptionHandler(StripeException.class)
    public ProblemDetail handleStripeException(StripeException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        problemDetail.setTitle("Payment Processing Error");
        return problemDetail;
    }
}
