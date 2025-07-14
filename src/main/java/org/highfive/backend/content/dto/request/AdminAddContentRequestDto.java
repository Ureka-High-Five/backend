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

    @NotNull
    private Long countryId;

    @NotBlank
    private String country;

    @NotBlank
    private String openDate;

    @Positive
    private Integer runningTime;

    @NotNull
    @PositiveOrZero
    private Integer viewCount;

    @Positive
    private Integer total_round;

    @NotBlank
    private String type;

    @NotNull
    private Long contentId;
}
