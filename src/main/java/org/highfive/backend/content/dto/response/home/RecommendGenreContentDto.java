package org.highfive.backend.content.dto.response.home;

import java.util.List;

public record RecommendGenreContentDto(
        String genreName,
        List<RecommendContentDto> contents
) {
}
