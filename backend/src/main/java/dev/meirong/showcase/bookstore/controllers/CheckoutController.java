package dev.meirong.showcase.bookstore.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.CheckoutDTO;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import dev.meirong.showcase.bookstore.services.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/checkouts/secure")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Checkout Controller")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @Operation(summary = "Get a number of current checkouts held by authenticated user.",
            description = "Returns a number of current checkouts as an int.")
    @GetMapping("/current-loans-count")
    public ResponseEntity<Integer> getCurrentCheckoutsCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer responseBody = checkoutService.getCurrentCheckoutsCount(userDetails.getUsername());
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "Get a list of all current checkouts held by authenticated user.",
            description = "Returns a list containing CheckoutDTO objects.")
    @GetMapping("/current-checkouts")
    public ResponseEntity<List<CheckoutDTO>> getCurrentCheckouts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<CheckoutDTO> responseBody = checkoutService.getCurrentCheckouts(userDetails.getUsername());
        return ResponseEntity.ok(responseBody);
    }
}
