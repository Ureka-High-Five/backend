package org.highfive.backend.shorts.dto.response;

public record ShortsItemDto(
        Long contentId,
        String contentTitle,
        Long shortsId,
        String shortsUrl
) {
}
