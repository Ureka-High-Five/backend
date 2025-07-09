package org.highfive.backend.content.dto.response.home;

import java.util.List;

public record HomeContentsResponseDto(
        List<RecommendContentDto> recommendContents, // 개인화 추천
        RecommendGenreContentDto recommendGenreContents,// 사용자가 가장 선호하는 장르 컨텐츠
        RecommendGenreContentDto recommendSecondGenreContents, // 사용자가 두 번째로 선호하는 장르 컨텐츠
        RecommendCurationDto recommendCuration,
        RecommendCurationDto recommendSecondCuration,
        RecommendContentDto randomContent // 랜덤 선정 컨텐츠
) {
}
