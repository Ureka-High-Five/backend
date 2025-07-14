package org.highfive.backend.content.dto.response;

public record OnboardingSelectContentResponseDto(
        long contentId,
        String thumbnailUrl,
        String title,
        int openYear
) {
}
