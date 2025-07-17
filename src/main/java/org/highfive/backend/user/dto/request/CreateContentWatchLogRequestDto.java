package org.highfive.backend.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateContentWatchLogRequestDto(
        @Positive
        @NotNull
        Long id,
        @Positive
        @NotNull
        Integer watchTime,
        @NotBlank
        String type
) {
}
