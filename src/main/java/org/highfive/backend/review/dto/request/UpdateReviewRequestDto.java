package org.highfive.backend.review.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateReviewRequestDto(
        String review,
        @NotNull
        int rating
) {
}
