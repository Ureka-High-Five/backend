package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateReviewRequestDto(
        String review,
        @NotNull
        int rating
) {
}
