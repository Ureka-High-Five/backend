package org.highfive.backend.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.TopContentByGenreDto;
import org.highfive.backend.content.repository.ContentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int INIT_CONTENT_CNT = 6;

    private final ContentRepository contentRepository;

    public List<OnboardingInitContentsResponseDto> getDistinctGenreTopContents() {
        List<TopContentByGenreDto> topContents = contentRepository.findTopContentPerGenre(INIT_CONTENT_CNT);

        return topContents.stream()
                .map(dto -> new OnboardingInitContentsResponseDto(dto.getId(), dto.getThumbnailUrl(), dto.getTitle()))
                .toList();
    }
}
