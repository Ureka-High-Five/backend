package org.highfive.backend.shorts.dto.response;

public record GetShortsCommentResponseDto(
        Long commentId,
        String comment,
        String userName,
        String profileUrl,
        Long userId
) {
}
