package org.highfive.backend.content.dto.response;

import java.time.Instant;

public record MostPopularContentPerGenreDto(
        Long id,
        String thumbnailUrl,
        String title,
        String genreName,
        Long rank,
        int openYear
) {}
