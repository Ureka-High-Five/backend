package org.highfive.backend.shorts.dto.response;

public record ShortsCommentsByIdResponseDto (
        String username,
        String profileUrl,
        String comment,
        Long userId,
        String createdAt
)
{
}
