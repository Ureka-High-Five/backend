package org.highfive.backend.content.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.MetaInfoContentsFixture;
import org.highfive.backend.common.fixture.MetaInfoFixture;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private MetaInfoContentsRepository metaInfoContentsRepository;

    @Mock
    private FastApiClient fastApiClient;

    @InjectMocks
    private ContentService contentService;
    @Mock
    private PreferMetaInfoRepository preferMetaInfoRepository;

    private List<MostPopularContentPerGenreDto> stubData;

    @BeforeEach
    void setUp() {
        stubData = List.of(
                new MostPopularContentPerGenreDto(1L, "s3://1", "A의 모험", 10, "장르1", 1L, 2024),
                new MostPopularContentPerGenreDto(2L, "s3://2", "B의 비밀", 10, "장르2", 2L, 2024)
        );
    }

    @DisplayName("온보딩 초기 - ContentRepository의 리턴 타입이 ContentService의 리턴 타입으로 잘 매핑된다")
    @Test
    void getDistinctGenreTopContents_success() {
        // given
        when(contentRepository.findTopContentPerGenre(6)).thenReturn(stubData);

        // when
        List<OnboardingInitContentsResponseDto> result = contentService.getDistinctGenreTopContents();

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("contentId", "thumbnailUrl", "title")
                .containsExactly(
                        tuple(1L, "s3://1", "A의 모험"),
                        tuple(2L, "s3://2", "B의 비밀")
                );

        verify(contentRepository).findTopContentPerGenre(6);
        verifyNoMoreInteractions(contentRepository, metaInfoContentsRepository,
                fastApiClient, preferMetaInfoRepository);
    }

    @Test
    @DisplayName("온보딩 초기 - 장르별 인기있는 작품 리스트가 빈 리스트인 경우 예외가 발생한다")
    void getDistinctGenreTopContents_emptyResult() {
        // given
        when(contentRepository.findTopContentPerGenre(6))
                .thenReturn(List.of());

        // when
        assertThatThrownBy(() -> contentService.getDistinctGenreTopContents())
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ContentErrorCode.CONTENT_NOT_FOUND);
                });

        // then
        verify(contentRepository).findTopContentPerGenre(6);
        verifyNoMoreInteractions(contentRepository, metaInfoContentsRepository,
                fastApiClient, preferMetaInfoRepository);
    }

    @Test
    @DisplayName("온보딩 초기 - ContentRepository가 예외를 던지면 서비스도 예외를 전파한다")
    void getDistinctGenreTopContents_repositoryThrows() {
        // given
        when(contentRepository.findTopContentPerGenre(6))
                .thenThrow(new IllegalStateException("DB 오류"));

        // when, then
        assertThatThrownBy(() -> contentService.getDistinctGenreTopContents())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB 오류");

        verify(contentRepository).findTopContentPerGenre(6);
        verifyNoMoreInteractions(contentRepository, metaInfoContentsRepository,
                fastApiClient, preferMetaInfoRepository);
    }


    @Test
    @DisplayName("존재하는 content일 경우 content의 상세 정보를 반환한다")
    public void getContentDetail_correctContentId() {

        //given
        Long contentId = 1L;
        Content content = ContentFixture.createContent(contentId);

        MetaInfo directorInfo = MetaInfoFixture.createMetaInfo("감독", MetaType.DIRECTOR);
        MetaInfo actorInfo = MetaInfoFixture.createMetaInfo("배우1", MetaType.ACTOR);
        MetaInfo actorInfo2 = MetaInfoFixture.createMetaInfo("배우2", MetaType.ACTOR);
        MetaInfo genreInfo = MetaInfoFixture.createMetaInfo("로맨스", MetaType.GENRE);

        List<MetaInfoContents> metaInfoContents = List.of(
                MetaInfoContentsFixture.createMetaInfoContents(directorInfo, content),
                MetaInfoContentsFixture.createMetaInfoContents(actorInfo, content),
                MetaInfoContentsFixture.createMetaInfoContents(actorInfo2, content),
                MetaInfoContentsFixture.createMetaInfoContents(genreInfo, content)
        );

        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
        when(metaInfoContentsRepository.findByContentId(contentId)).thenReturn(metaInfoContents);

        //when
        Response<ContentDetailResponseDto> response = contentService.getContentDetail(contentId);

        //then
        assertEquals(SuccessCode.OK.getCode(), response.code());

        ContentDetailResponseDto result = response.content();
        assertNotNull(result);
        assertEquals("감독", result.director());
        assertEquals(List.of("배우1", "배우2"), result.actors());
        assertEquals(List.of("로맨스"), result.contentGenres());
        assertEquals(2025, result.openYear());
        assertEquals("테스트 제목", result.contentTitle());

    }

    @Test
    @DisplayName("컨텐츠가 없으면 예외를 던진다")
    public void getContentDetail_noContentId() {
        //given
        Long contentId = 93498579L;
        when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contentService.getContentDetail(contentId);
        });

        //then
        assertEquals(ContentErrorCode.CONTENT_NOT_FOUND, exception.getErrorCode());

    }
}