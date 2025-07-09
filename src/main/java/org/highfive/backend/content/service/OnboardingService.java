package org.highfive.backend.content.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.global.RecommendType;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final int RESULT_CONTENT_COUNT = 3;

    private final FastApiClient fastApiClient;
    private final ContentRepository contentRepository;

    public List<OnboardingSelectContentResponseDto> getContentBySelectedContent(
            final OnboardingSelectContentRequestDto request) {
        final List<Long> selectedContentIds = request.selectedContentIds();
        List<Map<String, Object>> contentGenresByContentIds = contentRepository.findContentGenresByContentIds(selectedContentIds);

        List<Entry<String, Integer>> sortedGenres = countGenre(contentGenresByContentIds);
        List<String> topGenres = sortedGenres.stream()
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        return getOnboardingSelectContentResponseDtos(topGenres);

        // 추후 유사도 계산 도입할 때 사용
//        final List<Long> contentIds = fastApiClient.recommendContentsByContent(selectedContentIds.getLast(), RecommendType.GENRE);
//        List<Content> contents = contentRepository.findAllById(contentIds);
//        List<Content> result = duplicateFilter(contents, request);
//        return result.stream().map(ContentMapper::toOnboardingSelectContentResponseDto).toList();
    }

    private static List<Entry<String, Integer>> countGenre(List<Map<String, Object>> contentGenresByContentIds) {
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

    private List<OnboardingSelectContentResponseDto> getOnboardingSelectContentResponseDtos(List<String> topGenres) {
        List<OnboardingContentDto> result = contentRepository.findContentsByGenres(topGenres, topGenres.size());
        for (String genre : topGenres) {
            if (result.size() == RESULT_CONTENT_COUNT) {
                return result.stream().map(c -> new OnboardingSelectContentResponseDto(c.id(), c.postUrl(), c.title(),
                        c.openDate().getYear())).toList();
            }
            result.addAll(contentRepository.findContentsWithOnlyOneGenre(genre));
        }

        return result.stream().map(c -> new OnboardingSelectContentResponseDto(c.id(), c.postUrl(), c.title(),
                c.openDate().getYear())).toList();
    }

    private List<Content> duplicateFilter(final List<Content> contents, final OnboardingSelectContentRequestDto request) {
        final List<Content> result = new ArrayList<>();
        for (Content content : contents) {
            if (request.selectedContentIds().contains(content.getId())) {
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
