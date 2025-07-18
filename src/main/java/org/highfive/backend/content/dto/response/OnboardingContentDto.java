package org.highfive.backend.content.dto.response;

import java.time.LocalDateTime;

public record OnboardingContentDto(
        Long id,
        String thumbnailUrl,
        String title,
        LocalDateTime openDate,
        Long genreMatchCount
) {
}
