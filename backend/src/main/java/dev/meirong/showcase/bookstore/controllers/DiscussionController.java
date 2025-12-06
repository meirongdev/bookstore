package dev.meirong.showcase.bookstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.meirong.showcase.bookstore.dto.DiscussionDTO;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;
import dev.meirong.showcase.bookstore.services.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/discussions/secure")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Discussion Controller")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;

    @Operation(summary = "Get a paginated list of all discussions.",
            description = "Returns a Page containing DiscussionDTO objects for authenticated user.")
    @GetMapping
    public ResponseEntity<Page<DiscussionDTO>> findAllByUserEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "discussions-per-page") Integer discussionsPerPage) {

        Page<DiscussionDTO> responseBody = discussionService.findAllByUserEmail(
                userDetails.getUsername(),
                PageRequest.of(page, discussionsPerPage));
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "Create a new discussion entity.",
            description = "Adds a new discussion entity marked as open to a DataBase. Requires a valid DiscussionDTO object as a request body.")
    @PostMapping("add-discussion")
    public ResponseEntity<DiscussionDTO> addDiscussion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid DiscussionDTO discussionDTO,
            BindingResult bindingResult) {

        DiscussionDTO responseBody = discussionService.addDiscussion(
                userDetails.getUsername(),
                discussionDTO,
                bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}
