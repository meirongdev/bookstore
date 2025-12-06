package dev.meirong.showcase.bookstore.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        User userForValidation = (User) target;

        if (userForValidation.getDateOfBirth().isBefore(LocalDate.of(1900, 1, 1))) {
            errors.rejectValue("dateOfBirth", "Birth date cannot be before 01-01-1900");
        }

        Optional<User> userFromBD = userRepository.findByEmail(userForValidation.getEmail());

        if (userFromBD.isPresent()) {
            errors.rejectValue("email", "User with this email is already registered");
        }
    }
}
