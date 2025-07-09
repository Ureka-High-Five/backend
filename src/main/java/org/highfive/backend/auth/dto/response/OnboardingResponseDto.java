package org.highfive.backend.auth.dto.response;

public record OnboardingResponseDto(
        Long userId,
        String nickname,
        boolean isNew
) {
}
