package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateReviewRequestDto(
        @NotNull
        String review,
        int rating
) {
}
