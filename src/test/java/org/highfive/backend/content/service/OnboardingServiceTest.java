package org.highfive.backend.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.repository.QueryDslContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock QueryDslContentRepository queryDslRepo;

    @InjectMocks OnboardingService onboardingService;

    @Test
    @DisplayName("온보딩 선택 - 선택한 콘텐츠 ID들을 기반으로 중복을 제외한 3개 이하의 추천 콘텐츠를 반환한다")
    void getContentBySelectedContent_returnsThreeDistinctRecommendations() {
        // given
        List<Long> selectedIds = List.of(1L, 2L);
        OnboardingSelectContentRequestDto req =
                new OnboardingSelectContentRequestDto(selectedIds);

        when(queryDslRepo.findTopGenresByContentIds(selectedIds))
                .thenReturn(List.of(
                        new GenreCountDto("Action", 3L),
                        new GenreCountDto("Drama",  2L)
                ));

        List<OnboardingContentDto> repoReturn = List.of(
                new OnboardingContentDto(1L, "url1", "title1", LocalDateTime.of(2024,1,1,1,1,1), 2L),
                new OnboardingContentDto(2L, "url2", "title2", LocalDateTime.of(2023,5,5,1,1,1), 2L),
                new OnboardingContentDto(3L, "url3", "title3", LocalDateTime.of(2022,3,3,1,1,1), 2L),
                new OnboardingContentDto(4L, "url4", "title4", LocalDateTime.of(2021,2,2,1,1,1), 2L),
                new OnboardingContentDto(5L, "url5", "title5", LocalDateTime.of(2020,1,1,1,1,1), 2L)
        );
        when(queryDslRepo.findContentsByGenresOrderByMatchCountDesc(
                eq(List.of("Action", "Drama"))))
                .thenReturn(repoReturn);

        // when
        List<OnboardingSelectContentResponseDto> actual = onboardingService.getContentBySelectedContent(req);

        // then
        assertThat(actual).containsExactly(
                        new OnboardingSelectContentResponseDto(3L, "url3", "title3", 2022),
                        new OnboardingSelectContentResponseDto(4L, "url4", "title4", 2021),
                        new OnboardingSelectContentResponseDto(5L, "url5", "title5", 2020)
                );

        verify(queryDslRepo).findTopGenresByContentIds(selectedIds);
        verify(queryDslRepo).findContentsByGenresOrderByMatchCountDesc(List.of("Action", "Drama"));
        verifyNoMoreInteractions(queryDslRepo);
    }
}
