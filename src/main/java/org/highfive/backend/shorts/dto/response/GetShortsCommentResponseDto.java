package org.highfive.backend.shorts.dto.response;

public record GetShortsCommentResponseDto(
        String comment,
        String userName,
        String profileUrl,
        Long userId
) {
}
