package org.highfive.backend.user.dto.response;

public record UserInfoResponseDto(
        long userId,
        String name,
        String email,
        String role,
        String profileUrl
) {
}
