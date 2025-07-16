package org.highfive.backend.shorts.dto.request;

import jakarta.validation.constraints.NotNull;

public record ShortsLikeRequestDto(
        @NotNull
        long shortsId,
        @NotNull
        long time
) {
}
