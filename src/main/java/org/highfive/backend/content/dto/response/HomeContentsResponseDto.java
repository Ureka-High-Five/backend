package org.highfive.backend.content.dto.response;

import java.util.List;
import java.util.Map;

public record HomeContentsResponseDto(
        MainRecommendDto mainRecommend,
        List<PersonalRecommendDto> personalRecommends,
        Map<String, List<GenreContentDto>> genre,
        List<CurationDto> curation
) {
    public record MainRecommendDto(
            Long contentId,
            String posterUrl,
            String description,
            List<String> genre,
            String title,
            String videoUrl
    ) {
    }

    public record PersonalRecommendDto(
            Long contentId,
            String thumbnailUrl
    ) {
    }

    public record GenreContentDto(
            Long contentId,
            String thumbnailUrl
    ) {
    }

    public record CurationDto(
            Long curationId,
            Long userId,
            String userName,
            List<CurationContentsDto> contents,
            String title,
            String profileUrl
    ) {
    }

    public record CurationContentsDto(
            Long contentId,
            String thumbnailUrl
    ) {
    }
}
