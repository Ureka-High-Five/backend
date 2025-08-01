package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;

@Builder
public record AdminAddContentRequestDto(

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotBlank
        String videoUrl,

        @NotBlank
        String postUrl,

        @NotBlank
        String shortsUrl,

        @NotBlank
        String countryName,

        @NotBlank
        String openDate,

        @Positive
        Integer runningTime,

        @Positive
        Integer totalRound,

        @NotBlank
        String type,

        @NotEmpty
        List<String> genres,

        @NotEmpty
        List<String> actors,

        @NotBlank
        String director,

        @NotNull
        @Positive
        Integer trailerTime,

        @NotBlank
        String uuid
) {
}



