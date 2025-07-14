package org.highfive.backend.content.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.repository.querydsl.QueryDslMetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiVectorFromGenresDto;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class AdminContentServiceTest {

    @InjectMocks
    AdminContentService adminContentService;

    @Mock
    ContentRepository contentRepository;

    @Mock
    MetaInfoContentsRepository metaInfoContentsRepository;

    @Mock
    private QueryDslMetaInfoRepository queryDslMetaInfoRepository;

    @Mock
    private FastApiClient fastApiClient;

    @Test
    @Transactional
    @DisplayName("컨텐츠 추가 - 컨텐츠가 정상적으로 추가됩니다.")
    void addContents_success() {
        // given
        given(fastApiClient.vectorFromGenres(List.of("thriller"))).willReturn(new FastApiVectorFromGenresDto("MOCK_VEC"));

        MetaInfo actor    = MetaInfo.builder().name("톰 크루즈").type( MetaType.ACTOR).build();
        MetaInfo director = MetaInfo.builder().name("딘 데블로이스").type(MetaType.DIRECTOR).build();
        MetaInfo country  = MetaInfo.builder().name("KR").type(MetaType.COUNTRY).build();

        given(queryDslMetaInfoRepository.findByNameAndType("톰 크루즈", MetaType.ACTOR)).willReturn(actor);
        given(queryDslMetaInfoRepository.findByNameAndType("딘 데블로이스", MetaType.DIRECTOR)).willReturn(director);
        given(queryDslMetaInfoRepository.findByNameAndType("KR", MetaType.COUNTRY)).willReturn(country);

        given(contentRepository.save(any(Content.class)))
                .willAnswer(inv -> {
                    return ContentFixture.createContent(1L);
                });

        given(metaInfoContentsRepository.save(any(MetaInfoContents.class)))
                .willAnswer(inv -> inv.getArgument(0));

        AdminAddContentRequestDto request = getAdminAddContentRequestDto();

        // when
        Response<AdminAddContentResponseDto> response = adminContentService.addContent(request, UserFixture.createAdmin(1L));

        // then
        Long contentId = response.content().getContentId();
        Assertions.assertNotNull(contentId);
        Assertions.assertNotNull(contentRepository.findById(contentId));
        Assertions.assertNotNull(metaInfoContentsRepository.findByContentId(contentId));
    }

    @Test
    @DisplayName("컨텐츠 추가 - 잘못된 장르 전달 시 FastAPI 서버 예외 전파")
    void invalidGenreName_test() {
        given(fastApiClient.vectorFromGenres(List.of("thriller")))
                .willThrow(new IllegalArgumentException("unknown genre"));

        AdminAddContentRequestDto req = getAdminAddContentRequestDto();
        assertThrows(IllegalArgumentException.class, () -> adminContentService.addContent(req, UserFixture.createAdmin(1L)));

        verify(fastApiClient).vectorFromGenres(List.of("thriller"));
    }

    @Test
    @DisplayName("컨텐츠 추가 - 어드민이 아닌 경우 예외를 반환합니다.")
    void accessDenied_test() {
        // given
        User user = UserFixture.createUser(1L);
        AdminAddContentRequestDto req = getAdminAddContentRequestDto();

        // when, then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> adminContentService.addContent(req, user)
        );

        assert ex.getErrorCode() == GlobalErrorCode.ACCESS_DENIED;
    }

    private static AdminAddContentRequestDto getAdminAddContentRequestDto() {
        return AdminAddContentRequestDto.builder()
                .title("test-title")
                .description("test-desc")
                .videoUrl("test-videourl")
                .postUrl("test-posturl")
                .countryName("KR")
                .openDate("2025-01-01")
                .runningTime(100)
                .totalRound(10)
                .type("MOVIE")
                .genres(List.of("thriller"))
                .actors(List.of("톰 크루즈"))
                .director("딘 데블로이스")
                .build();
    }
}