package dev.meirong.showcase.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {

    private Long id;

    private String userEmail;

    private String userFirstName;

    @JsonProperty("createdAt")
    private Long createdAt; // Unix timestamp in milliseconds

    @JsonProperty("updatedAt")
    private Long updatedAt; // Unix timestamp in milliseconds

    @NotNull(message = "Rating must be present")
    private Double rating;

    private String reviewDescription;
}
