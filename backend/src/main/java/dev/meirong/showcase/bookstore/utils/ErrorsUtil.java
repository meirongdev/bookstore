package dev.meirong.showcase.bookstore.utils;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import dev.meirong.showcase.bookstore.exceptions.BookException;
import dev.meirong.showcase.bookstore.exceptions.DiscussionException;
import dev.meirong.showcase.bookstore.exceptions.GenreException;
import dev.meirong.showcase.bookstore.exceptions.PaymentException;
import dev.meirong.showcase.bookstore.exceptions.UserException;
import dev.meirong.showcase.bookstore.exceptions.ReviewException;

import java.util.List;

public class ErrorsUtil {
  private ErrorsUtil() {
    // Utility class
  }
  public static void returnBookError(String generalMessage, BindingResult bindingResult, HttpStatus httpStatus) {

    throw new BookException(buildErrorMessage(generalMessage, bindingResult), httpStatus);
  }

  public static void returnDiscussionError(String generalMessage, BindingResult bindingResult, HttpStatus httpStatus) {

    throw new DiscussionException(buildErrorMessage(generalMessage, bindingResult), httpStatus);
  }

  public static void returnGenreError(String generalMessage, BindingResult bindingResult, HttpStatus httpStatus) {

    throw new GenreException(buildErrorMessage(generalMessage, bindingResult), httpStatus);
  }

  public static void returnPaymentError(String message, HttpStatus httpStatus) {

    throw new PaymentException(message, httpStatus);
  }

  public static void returnUserError(String generalMessage, BindingResult bindingResult, HttpStatus httpStatus) {

    throw new UserException(buildErrorMessage(generalMessage, bindingResult), httpStatus);
  }

  public static void returnReviewError(String generalMessage, BindingResult bindingResult, HttpStatus httpStatus) {

    throw new ReviewException(buildErrorMessage(generalMessage, bindingResult), httpStatus);
  }

  private static String buildErrorMessage(String generalMessage, BindingResult bindingResult) {

    StringBuilder errorMessage = new StringBuilder();

    if (bindingResult != null) {

      List<FieldError> errors = bindingResult.getFieldErrors();

      for (FieldError error : errors) {
        errorMessage.append(error.getField())
            .append(": ")
            .append(error.getDefaultMessage() == null ? error.getCode() : error.getDefaultMessage())
            .append("; ");
      }

    }

    return generalMessage + " " + errorMessage;
  }
}
