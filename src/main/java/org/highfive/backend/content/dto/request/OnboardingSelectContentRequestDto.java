package org.highfive.backend.content.dto.request;

public record OnboardingSelectContentRequestDto(
        long contentId,
        String posterUrl,
        String title,
        int openYear
) {
}
