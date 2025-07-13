package org.highfive.backend.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.service.TokenService;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.MetaInfoRepository;
import org.highfive.backend.content.repository.QueryDslContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiOnboardingResponseDto;
import org.highfive.backend.global.client.fastapi.exception.FastApiErrorCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.global.util.WeightManager;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.preference.PreferMetaInfo;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    FastApiClient fastApiClient;
    @Mock
    WeightManager weightManager;
    @Mock
    UserRepository userRepository;
    @Mock
    MetaInfoRepository metaRepo;
    @Mock
    PreferMetaInfoRepository preferRepo;
    @Mock
    TokenService tokenService;
    @Mock
    QueryDslContentRepository qdslRepo;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("온보딩 제출 - 기본 정보, 벡터, 가중치, 토큰이 모두 저장된다")
    void initUser_success() {
        // given
        User user = User.builder()
                .id(1L)
                .kakaoUserId("kakao123")
                .build();
        SubmitOnboardingRequestDto req =
                new SubmitOnboardingRequestDto(1L, List.of(10L,11L), 1998, Gender.MALE, "홍길동");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 10, 11번 컨텐츠의 장르 리턴
        when(qdslRepo.findContentGenresByContentIds(List.of(10L,11L)))
                .thenReturn(List.of(
                        Map.of("contentId",10L,"genreName","Action"),
                        Map.of("contentId",11L,"genreName","Action"),
                        Map.of("contentId",10L,"genreName","Drama")
                ));

        // fastApi 응답
        when(fastApiClient.onboardingSubmit(Map.of("Action",2,"Drama",1)))
                .thenReturn(new FastApiOnboardingResponseDto("[0.1,0.2,0.3]"));

        // 가중치 설정
        when(weightManager.calcWeight(Map.of("Action",2,"Drama",1)))
                .thenReturn(Map.of("Action",0.67,"Drama",0.33));

        MetaInfo actionMeta = new MetaInfo(100L,"Action", MetaType.GENRE,null);
        MetaInfo dramaMeta  = new MetaInfo(101L,"Drama", MetaType.GENRE,null);
        when(metaRepo.findGenreMetaIdByName("Action")).thenReturn(List.of(actionMeta));
        when(metaRepo.findGenreMetaIdByName("Drama")).thenReturn(List.of(dramaMeta));

        when(tokenService.generateAccessToken(eq("kakao123"), anyList())).thenReturn("access");
        when(tokenService.generateRefreshToken(eq("kakao123"), anyList())).thenReturn("refresh");

        // when
        Response<TokenResponseDto> res = userService.initUser(req);

        // then
        assertThat(res.code()).isEqualTo(20000);
        assertThat(res.content().accessToken()).isEqualTo("access");
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getEmbedding()).isEqualTo("[0.1,0.2,0.3]");

        verify(preferRepo, times(2)).save(any(PreferMetaInfo.class));
    }

    @Test
    @DisplayName("온보딩 제출 - FastAPI 오류 시 BusinessException이 발생한다")
    void initUser_fastApiFailure_throwsBusinessException() {
        // given
        User user = User.builder()
                .id(1L)
                .kakaoUserId("kakao123")
                .build();
        SubmitOnboardingRequestDto req =
                new SubmitOnboardingRequestDto(1L, List.of(10L, 11L), 1998, Gender.MALE, "홍길동");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(qdslRepo.findContentGenresByContentIds(List.of(10L,11L)))
                .thenReturn(List.of(
                        Map.of("contentId",10L,"genreName","Action"),
                        Map.of("contentId",11L,"genreName","Action")
                ));

        when(fastApiClient.onboardingSubmit(Map.of("Action",2)))
                .thenThrow(new BusinessException(FastApiErrorCode.FAST_API_RESPONSE_ERROR));

        // when, then
        assertThatThrownBy(() -> userService.initUser(req))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode())
                            .isEqualTo(FastApiErrorCode.FAST_API_RESPONSE_ERROR);
                });

        verify(weightManager, times(0)).calcWeight(anyMap());
        verify(preferRepo,   times(0)).save(any(PreferMetaInfo.class));
    }
}
