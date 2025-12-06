package dev.meirong.showcase.bookstore.security.services;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.entities.Role;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.security.dto.requests.UserLoginDTO;
import dev.meirong.showcase.bookstore.security.dto.requests.UserRegistrationDTO;
import dev.meirong.showcase.bookstore.security.dto.responses.AuthenticationResponse;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import dev.meirong.showcase.bookstore.security.jwt.JwtUtils;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import dev.meirong.showcase.bookstore.utils.validators.UserValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerUser(UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {

        User user = new User();

        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setDateOfBirth(userRegistrationDTO.getDateOfBirth());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setRegisteredAt(Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("UTC")).toLocalDateTime());

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnUserError("Some fields are invalid.", bindingResult, HttpStatus.FORBIDDEN);
        }

        userRepository.save(user);

        String jwtToken = jwtUtils.generateToken(new CustomUserDetails(user));

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticateUser(UserLoginDTO userLoginDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnUserError("Some fields are invalid.", bindingResult, HttpStatus.FORBIDDEN);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));
        } catch (BadCredentialsException e) {
            ErrorsUtil.returnUserError("Login or password is incorrect.", bindingResult, HttpStatus.FORBIDDEN);
        }

        Optional<User> user = userRepository.findByEmail(userLoginDTO.getEmail());

        // This case is actually handled by "catch (BadCredentialsException e)" above
        if (user.isEmpty()) {
            ErrorsUtil.returnUserError("User with such email is not found. Please check the input fields.", bindingResult, HttpStatus.NOT_FOUND);
        }

        String jwtToken = jwtUtils.generateToken(new CustomUserDetails(user.get()));

        return new AuthenticationResponse(jwtToken);
    }
}
