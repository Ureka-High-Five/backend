package org.highfive.backend.shorts.dto.request;

import jakarta.validation.constraints.NotNull;

public record ShortsDislikeRequestDto(
        @NotNull
        long shortsId
) {
}
