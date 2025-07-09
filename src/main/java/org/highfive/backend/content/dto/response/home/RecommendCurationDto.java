package org.highfive.backend.content.dto.response.home;

import java.util.List;

public record RecommendCurationDto(
        String userName,
        String profileUrl,
        String curationTitle,
        List<RecommendContentDto> contents
) {
}
