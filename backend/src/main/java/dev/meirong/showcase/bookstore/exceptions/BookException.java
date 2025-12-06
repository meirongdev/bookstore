package dev.meirong.showcase.bookstore.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BookException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BookException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
