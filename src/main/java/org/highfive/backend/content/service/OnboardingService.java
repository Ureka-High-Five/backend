package org.highfive.backend.content.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
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
        final List<Long> selectedContentId = request.selectedContentId();
        final List<Long> contentIds = fastApiClient.recommendContentsByContent(selectedContentId.getLast(), RecommendType.GENRE);
        List<Content> contents = contentRepository.findAllById(contentIds);
        List<Content> result = duplicateFilter(contents, request);
        return result.stream().map(ContentMapper::toOnboardingSelectContentResponseDto).toList();
    }

    private List<Content> duplicateFilter(final List<Content> contents, final OnboardingSelectContentRequestDto request) {
        final List<Content> result = new ArrayList<>();
        for (Content content : contents) {
            if (request.selectedContentId().contains(content.getId())) {
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
