package dev.meirong.showcase.bookstore.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByEmail(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No users found with such email.");
        }

        return new CustomUserDetails(user.get());
    }
}
