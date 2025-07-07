package org.highfive.backend.auth.controller.dto.response;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
