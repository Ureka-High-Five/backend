package org.highfive.backend.shorts.controller;

public record ShortsGetByIdResponseDto(
        String shortsUrl,
        Long contentId,
        String contentTitle
) {
}
