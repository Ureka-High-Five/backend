package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminAddContentRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String videoUrl;

    @NotBlank
    private String postUrl;

    @NotBlank
    private String countryName;

    @NotBlank
    private String openDate;

    @Positive
    private Integer runningTime;

    @Positive
    private Integer totalRound;

    @NotBlank
    private String type;
}
