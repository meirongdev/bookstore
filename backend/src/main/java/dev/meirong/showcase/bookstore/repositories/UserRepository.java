package dev.meirong.showcase.bookstore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.meirong.showcase.bookstore.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
