package org.highfive.backend.shorts.dto.response;

public record ShortsCommentsByTimeResponseDto(
       int time,
       String username,
       String profileUrl,
       String comment,
       Long userId
) {
}
