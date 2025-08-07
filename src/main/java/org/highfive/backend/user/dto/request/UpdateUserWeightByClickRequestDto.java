package org.highfive.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserWeightByClickRequestDto(
        @NotNull
        Long contentId
) {
}
