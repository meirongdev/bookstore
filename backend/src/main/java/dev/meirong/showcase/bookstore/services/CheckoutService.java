package dev.meirong.showcase.bookstore.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.CheckoutDTO;
import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.Checkout;
import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.CheckoutRepository;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final EntityMapper entityMapper;
    private final CheckoutRepository checkoutRepository;
    private final UserRepository userRepository;

    public int getCurrentCheckoutsCount(String userEmail) {

        User user = getUserFromRepository(userEmail);

        return checkoutRepository.findByCheckoutHolder(user).size();
    }

    public List<CheckoutDTO> getCurrentCheckouts(String userEmail) {

        User user = getUserFromRepository(userEmail);

        List<CheckoutDTO> response = new ArrayList<>();

        List<Checkout> checkouts = checkoutRepository.findByCheckoutHolder(user);

        Map<Checkout, Book> checkoutBookMap = new HashMap<>();

        for (Checkout checkout : checkouts) {
            checkoutBookMap.put(checkout, checkout.getCheckedOutBook());
        }

        for (Map.Entry<Checkout, Book> entry : checkoutBookMap.entrySet()) {
            LocalDate d1 = entry.getKey().getReturnDate();
            LocalDate d2 = LocalDate.now();

            long differenceInTime = ChronoUnit.DAYS.between(d2, d1);

            BookDTO bookDTO = convertToBookDTO(entry.getValue());

            CheckoutDTO checkoutDTO = new CheckoutDTO();
            checkoutDTO.setBookDTO(bookDTO);
            checkoutDTO.setDaysLeft((int) differenceInTime);

            response.add(checkoutDTO);
        }

        return response;
    }

    private User getUserFromRepository(String userEmail) {

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isPresent()) {
            return user.get();
        } else {
            ErrorsUtil.returnUserError("User with such email is not found.", null, HttpStatus.NOT_FOUND);
            return null; // or throw an exception if ErrorsUtil does not throw
        }
    }

    private BookDTO convertToBookDTO(Book book) {
        return entityMapper.toBookDTO(book);
    }
}
