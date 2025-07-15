package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotNull;

public record ShortsLikeRequestDto(
        @NotNull
        long shortsId,
        @NotNull
        long time
) {
}
