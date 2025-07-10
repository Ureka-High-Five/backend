package org.highfive.backend.content.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.QueryDslContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final int RESULT_CONTENT_COUNT = 3;

    private final FastApiClient fastApiClient;
    private final QueryDslContentRepository queryDslContentRepository;

    public List<OnboardingSelectContentResponseDto> getContentBySelectedContent(
            final OnboardingSelectContentRequestDto request) {
        List<GenreCountDto> topGenresByContentIds = queryDslContentRepository.findTopGenresByContentIds(request.selectedContentIds());
        return getOnboardingSelectContentResponseDtos(topGenresByContentIds.stream().map((GenreCountDto::genre)).toList(), request);

        // 추후 유사도 계산 도입할 때 사용
//        final List<Long> contentIds = fastApiClient.recommendContentsByContent(selectedContentIds.getLast(), RecommendType.GENRE);
//        List<Content> contents = contentRepository.findAllById(contentIds);
//        List<Content> result = duplicateFilter(contents, request);
//        return result.stream().map(ContentMapper::toOnboardingSelectContentResponseDto).toList();
    }

    private List<Entry<String, Integer>> countGenre(List<Map<String, Object>> contentGenresByContentIds) {
        Map<String, Integer> genreCount = new HashMap<>();
        for (Map<String, Object> map : contentGenresByContentIds) {
            for (Object genreObj : map.values()) {
                String genre = genreObj.toString();
                genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
            }
        }

        List<Entry<String, Integer>> sortedGenres = new ArrayList<>(genreCount.entrySet());

        sortedGenres.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));
        return sortedGenres;
    }

    private List<OnboardingSelectContentResponseDto> getOnboardingSelectContentResponseDtos(List<String> topGenres, OnboardingSelectContentRequestDto request) {
        List<OnboardingContentDto> result = queryDslContentRepository.findContentsByGenresOrderByMatchCountDesc(topGenres);
        result = duplicateFilter(result, request);
        return result.stream().map(
                c -> new OnboardingSelectContentResponseDto(c.id(), c.postUrl(), c.title(), c.openDate().getYear()))
                .toList();
    }

    private List<OnboardingContentDto> duplicateFilter(final List<OnboardingContentDto> contents, final OnboardingSelectContentRequestDto request) {
        final List<OnboardingContentDto> result = new ArrayList<>();
        for (OnboardingContentDto content : contents) {
            if (request.selectedContentIds().contains(content.id())) {
                continue;
            }
            result.add(content);
            if (result.size() == RESULT_CONTENT_COUNT) {
                break;
            }
        }
        return result;
    }
}
