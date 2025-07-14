package org.highfive.backend.content.dto.response;

import java.util.List;
import java.util.Map;

public record HomeContentsResponseDto(
        MainRecommendDto mainRecommend,
        List<PersonalRecommendDto> personalRecommends,
        Map<String, List<GenreContentDto>> genre,
        CurationDto curation
) {
    public record MainRecommendDto(
            String posterUrl,
            String description,
            List<String> genre
    ) {}

    public record PersonalRecommendDto(
            Long contentId,
            String thumbnailUrl
    ) {}

    public record GenreContentDto(
            Long contentId,
            String thumbnailUrl
    ) {}

    public record CurationDto(
    ) {}
}
