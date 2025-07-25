package org.highfive.backend.shorts.dto;

public record ShortsDto(
        Long id,
        String shortsUrl,
        String shortsThumbnail,
        Long contentId,
        String title
) {
}
