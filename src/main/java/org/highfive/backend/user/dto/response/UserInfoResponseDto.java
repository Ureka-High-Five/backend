package org.highfive.backend.user.dto.response;

public record UserInfoResponseDto(
        long userId,
        String userName,
        String email,
        String role,
        String profileUrl
) {
}
