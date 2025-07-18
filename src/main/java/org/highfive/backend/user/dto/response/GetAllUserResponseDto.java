package org.highfive.backend.user.dto.response;

public record GetAllUserResponseDto(
        String profileUrl,
        String username,
        String email,
        String role
) {
}
