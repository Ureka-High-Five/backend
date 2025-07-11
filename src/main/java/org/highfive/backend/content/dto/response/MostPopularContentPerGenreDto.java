package org.highfive.backend.content.dto.response;

public record MostPopularContentPerGenreDto(
        Long id,
        String thumbnailUrl,
        String title,
        String genreName,
        Long rank
) {}
