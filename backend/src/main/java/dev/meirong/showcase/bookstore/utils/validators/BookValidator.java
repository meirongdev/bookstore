package dev.meirong.showcase.bookstore.utils.validators;


import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.repositories.BookRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookValidator implements Validator {

    private final BookRepository bookRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Book bookForValidation = (Book) target;
        Optional<Book> bookFromDB = bookRepository.findByTitleAndAuthor(bookForValidation.getTitle(), bookForValidation.getAuthor());

        if (bookFromDB.isPresent() && bookFromDB.get().getAuthor().equals(bookForValidation.getAuthor())) {
            errors.rejectValue("title", "Book with this title from this author already exists");
        }
    }
}
