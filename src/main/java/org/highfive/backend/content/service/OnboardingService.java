package org.highfive.backend.content.service;

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

    private final FastApiClient fastApiClient;
    private final ContentRepository contentRepository;

    public List<OnboardingSelectContentResponseDto> getContentBySelectedContent(
            OnboardingSelectContentRequestDto request) {
        long selectedContentId = request.selectedContentId();
        List<Long> contentIds = fastApiClient.recommendContentsByContent(selectedContentId, RecommendType.GENRE);
        List<Content> contents = contentRepository.findAllById(contentIds);
        return contents.stream().map(ContentMapper::toOnboardingSelectContentResponseDto).toList();
    }
}
