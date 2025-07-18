package org.highfive.backend.user.dto.response;

public record GetAllUserResponseDto(
        Long userId,
        String profileUrl,
        String username,
        String email,
        String role
) {
}
