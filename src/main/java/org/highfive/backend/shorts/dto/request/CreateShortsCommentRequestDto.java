package org.highfive.backend.shorts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateShortsCommentRequestDto(
        @NotNull
        @NotBlank
        @Size(max = 255)
        String comment,
        @NotNull
        Long shortsId,
        @NotNull
        Long time
) {
}
