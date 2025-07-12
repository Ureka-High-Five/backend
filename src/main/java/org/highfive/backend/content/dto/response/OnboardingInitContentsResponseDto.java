package org.highfive.backend.content.dto.response;

public record OnboardingInitContentsResponseDto(
        long contentId,
        String thumbnailUrl,
        String title,
        int openYear
) {
}
