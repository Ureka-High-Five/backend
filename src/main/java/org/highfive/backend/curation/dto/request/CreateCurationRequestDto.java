package org.highfive.backend.curation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateCurationRequestDto(
        @NotBlank
        String title,

        @NotEmpty
        @NoDuplicate
        List<Long> contents,

        @NotBlank
        String thumbnail
) {
}