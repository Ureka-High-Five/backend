package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @NotEmpty
    private List<String> genres;

    @NotEmpty
    private List<String> actors;

    @NotBlank
    private String director;
}



