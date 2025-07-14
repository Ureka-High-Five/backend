package org.highfive.backend.content.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.repository.MetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.dto.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
    private MetaInfoRepository metaInfoRepository;

    @Mock
    private FastApiClient fastApiClient;

    @Test
    @Transactional
    @DisplayName("컨텐츠 추가 - 컨텐츠가 정상적으로 추가됩니다.")
    void addContents_success() {
        // given
        given(fastApiClient.calcVectorByGenres(List.of("thriller"))).willReturn("MOCK_VEC");

        MetaInfo actor    = MetaInfo.builder().name("톰 크루즈").type( MetaType.ACTOR).build();
        MetaInfo director = MetaInfo.builder().name("딘 데블로이스").type(MetaType.DIRECTOR).build();
        MetaInfo country  = MetaInfo.builder().name("KR").type(MetaType.COUNTRY).build();

        given(metaInfoRepository.findByActorName("톰 크루즈")).willReturn(actor);
        given(metaInfoRepository.findByDirectorName("딘 데블로이스")).willReturn(director);
        given(metaInfoRepository.findByCountryName("KR")).willReturn(country);

        given(contentRepository.save(any(Content.class)))
                .willAnswer(inv -> {
                    return ContentFixture.createContent(1L);
                });

        given(metaInfoContentsRepository.save(any(MetaInfoContents.class)))
                .willAnswer(inv -> inv.getArgument(0));

        AdminAddContentRequestDto request = getAdminAddContentRequestDto();

        // when
        Response<AdminAddContentResponseDto> response = adminContentService.addContent(request);

        // then
        Long contentId = response.content().getContentId();
        Assertions.assertNotNull(contentId);
        Assertions.assertNotNull(contentRepository.findById(contentId));
        Assertions.assertNotNull(metaInfoContentsRepository.findByContentId(contentId));
    }

    @Test
    @DisplayName("컨텐츠 추가 - 잘못된 장르 전달 시 FastAPI 서버 예외 전파")
    void invalidGenreName_test() {
        given(fastApiClient.calcVectorByGenres(List.of("thriller")))
                .willThrow(new IllegalArgumentException("unknown genre"));

        AdminAddContentRequestDto req = getAdminAddContentRequestDto();
        Assertions.assertThrows(IllegalArgumentException.class, () -> adminContentService.addContent(req));

        verify(fastApiClient).calcVectorByGenres(List.of("thriller"));
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