package org.highfive.backend.content.dto.response;

import java.time.LocalDate;

public record OnboardingContentDto(
        Long id,
        String postUrl,
        String title,
        LocalDate openDate,
        Long genreMatchCount
) {
}
