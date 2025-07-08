package org.highfive.backend.auth.dto.response;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
