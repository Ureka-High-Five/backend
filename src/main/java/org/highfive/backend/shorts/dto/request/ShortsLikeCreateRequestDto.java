package org.highfive.backend.shorts.dto.request;

import jakarta.validation.constraints.NotNull;

public record ShortsLikeCreateRequestDto(
        @NotNull
        long shortsId,
        @NotNull
        long time
) {
}
