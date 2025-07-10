package org.highfive.backend.content.dto.response;

public record OnboardingSelectContentResponseDto(
        long contentId,
        String posterUrl,
        String title,
        int openYear
) {
}
