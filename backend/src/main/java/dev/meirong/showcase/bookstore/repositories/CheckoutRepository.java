package dev.meirong.showcase.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.meirong.showcase.bookstore.entities.Book;
import dev.meirong.showcase.bookstore.entities.Checkout;
import dev.meirong.showcase.bookstore.entities.User;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Optional<Checkout> findByCheckoutHolderAndCheckedOutBook(User checkoutHolder, Book checkedOutBook);

    List<Checkout> findByCheckoutHolder(User checkoutHolder);
}
