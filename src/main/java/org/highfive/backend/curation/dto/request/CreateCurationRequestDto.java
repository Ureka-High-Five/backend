package org.highfive.backend.curation.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateCurationRequestDto(
        @NotBlank
        String title,

        @NotNull
        @NoDuplicate
        List<Long> contents,

        @Nullable
        String description,

        @NotBlank
        String thumbnail
) {
}