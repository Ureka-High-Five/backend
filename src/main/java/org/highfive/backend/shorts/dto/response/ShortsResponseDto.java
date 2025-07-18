package org.highfive.backend.shorts.dto.response;

public record ShortsResponseDto(
        Long shortsId,
        String shortsUrl,
        Long contentId,
        String contentTitle
) {
}
