package org.highfive.backend.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.service.TokenService;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.repository.MetaInfoRepository;
import org.highfive.backend.content.repository.QueryDslContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiOnboardingResponseDto;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.global.util.WeightManager;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    QueryDslContentRepository queryDslRepo;
    @InjectMocks
    OnboardingService onboardingService;
    @Mock
    private MetaInfoContentsRepository metaInfoContentsRepository;
    @Mock
    private PreferMetaInfoRepository preferMetaInfoRepository;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private FastApiClient fastApiClient;
    @Mock
    UserRepository userRepository;
    @Mock
    MetaInfoRepository metaInfoRepository;
    @Mock
    WeightManager weightManager;
    @Mock
    TokenService tokenService;

    private List<MostPopularContentPerGenreDto> stubData;

    @BeforeEach
    void setUp() {
        stubData = List.of(
                new MostPopularContentPerGenreDto(1L, "s3://1", "A의 모험", 10, "장르1", 1L, 2024),
                new MostPopularContentPerGenreDto(2L, "s3://2", "B의 비밀", 10, "장르2", 2L, 2024)
        );
    }

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
        Response<List<OnboardingSelectContentResponseDto>> actual = onboardingService.getContentBySelectedContent(req);

        // then
        assertThat(actual.content()).containsExactly(
                        new OnboardingSelectContentResponseDto(3L, "url3", "title3", 2022),
                        new OnboardingSelectContentResponseDto(4L, "url4", "title4", 2021),
                        new OnboardingSelectContentResponseDto(5L, "url5", "title5", 2020)
                );

        verify(queryDslRepo).findTopGenresByContentIds(selectedIds);
        verify(queryDslRepo).findContentsByGenresOrderByMatchCountDesc(List.of("Action", "Drama"));
        verifyNoMoreInteractions(queryDslRepo);
    }

    @DisplayName("온보딩 초기 - ContentRepository의 리턴 타입이 ContentService의 리턴 타입으로 잘 매핑된다")
    @Test
    void getDistinctGenreTopContents_success() {
        // given
        when(contentRepository.findTopContentPerGenre(6)).thenReturn(stubData);

        // when
        Response<List<OnboardingInitContentsResponseDto>> result = onboardingService.getDistinctGenreTopContents();

        // then
        AssertionsForInterfaceTypes.assertThat(result.content())
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
        assertThatThrownBy(() -> onboardingService.getDistinctGenreTopContents())
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    AssertionsForClassTypes.assertThat(be.getErrorCode()).isEqualTo(ContentErrorCode.CONTENT_NOT_FOUND);
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
        assertThatThrownBy(() -> onboardingService.getDistinctGenreTopContents())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB 오류");

        verify(contentRepository).findTopContentPerGenre(6);
        verifyNoMoreInteractions(contentRepository, metaInfoContentsRepository,
                fastApiClient, preferMetaInfoRepository);
    }

    @Test
    @DisplayName("온보딩 제출 - 온보딩 제출 후 사용자 정보 저장에 성공한다")
    void initUser_success() {
        // given
        User user = UserFixture.createUser(1L);
        String kakaoUserId = user.getKakaoUserId();
        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(user));

        given(fastApiClient.onboardingSubmit(anyMap()))
                .willReturn(new FastApiOnboardingResponseDto("vec-xyz"));

        given(weightManager.calcWeight(anyMap()))
                .willReturn(Map.of("Action", 0.7, "Comedy", 0.3));

        MetaInfo actionMeta = new MetaInfo(1L, "Action", MetaType.GENRE, null);
        given(metaInfoRepository.findGenreMetaIdByName("Action")).willReturn(List.of(actionMeta));
        MetaInfo comedyMeta = new MetaInfo(2L, "Comedy", MetaType.GENRE, null);
        given(metaInfoRepository.findGenreMetaIdByName("Comedy")).willReturn(List.of(comedyMeta));

        given(tokenService.generateAccessToken(eq(kakaoUserId), anyList())).willReturn("access");
        given(tokenService.generateRefreshToken(eq(kakaoUserId), anyList())).willReturn("refresh");

        SubmitOnboardingRequestDto req = new SubmitOnboardingRequestDto(
                1L,
                List.of(10L, 11L, 12L),
                Year.now().minusYears(25).getValue(),
                Gender.MALE,
                "Mike"
        );

        // when
        Response<TokenResponseDto> resp = onboardingService.initUser(req);

        // then
        assertThat(resp.code()).isEqualTo(20000);
        assertThat(resp.content())
                .extracting(TokenResponseDto::accessToken, TokenResponseDto::refreshToken, TokenResponseDto::isNew)
                .containsExactly("access", "refresh", true);

        assertThat(user.getName()).isEqualTo("Mike");
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(user.getGender()).isEqualTo(Gender.MALE);
        assertThat(user.getAge()).isEqualTo(25);
        assertThat(user.getEmbedding()).isEqualTo("vec-xyz");

        verify(preferMetaInfoRepository, times(2)).save(any());
    }
}
