package dev.meirong.showcase.bookstore.security.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.security.dto.requests.UserLoginDTO;
import dev.meirong.showcase.bookstore.security.dto.requests.UserRegistrationDTO;
import dev.meirong.showcase.bookstore.security.dto.responses.AuthenticationResponse;
import dev.meirong.showcase.bookstore.security.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user.",
            description = "Creates a new user entity and adds it into a DataBase. Requires a valid UserRegistrationDTO object as a request body. Returns a valid JWT token for new authenticated user.")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid UserRegistrationDTO userRegistrationDTO,
            BindingResult bindingResult) {

        AuthenticationResponse responseBody = authenticationService.registerUser(userRegistrationDTO, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Operation(summary = "Authenticate an existing user.",
            description = "Looks for a provided user credentials in a DataBase. Requires a valid UserLoginDTO object as a request body. Returns a valid JWT token for authenticated user.")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid UserLoginDTO userLoginDTO,
            BindingResult bindingResult) {

        AuthenticationResponse responseBody = authenticationService.authenticateUser(userLoginDTO, bindingResult);
        return ResponseEntity.ok(responseBody);
    }
}
