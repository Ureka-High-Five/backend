package org.highfive.backend.user.dto.response;

public record GetAllUserResponseDto(
        Long userId,
        String profileUrl,
        String userName,
        String email,
        String role
) {
}
