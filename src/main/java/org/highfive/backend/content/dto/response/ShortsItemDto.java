package org.highfive.backend.content.dto.response;

public record ShortsItemDto(
        Long contentId,
        String contentTitle,
        Long shortsId,
        String shortsUrl
) {
}
