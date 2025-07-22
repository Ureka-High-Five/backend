package org.highfive.backend.shorts.dto;

public record ShortsDto(
        Long id,
        String shortsUrl,
        Long contentId,
        String title
) {
}
