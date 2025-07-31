package org.highfive.backend.content.service;

import org.highfive.backend.common.fixture.AdminDtoFixture;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.request.AdminUpdateContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.dto.response.AdminUpdateContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.ContentVector;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.content.repository.jpa.ContentVectorRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiVectorFromGenresDto;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.infra.s3.service.S3Service;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.metadata.repository.jpa.MetaInfoRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.user.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @Mock
    private S3Service s3Service;

    @Mock
    private ShortsRepository shortsRepository;

    @Mock
    private ContentVectorRepository contentVectorRepository;

    @Test
    @Transactional
    @DisplayName("컨텐츠 추가 - 컨텐츠가 정상적으로 추가됩니다.")
    void addContents_success() {
        // given
        given(fastApiClient.vectorFromGenres(List.of("thriller"))).willReturn(new FastApiVectorFromGenresDto("MOCK_VEC"));

        MetaInfo actor    = MetaInfo.builder().name("톰 크루즈").type( MetaType.ACTOR).build();
        MetaInfo director = MetaInfo.builder().name("딘 데블로이스").type(MetaType.DIRECTOR).build();
        MetaInfo country  = MetaInfo.builder().name("KR").type(MetaType.COUNTRY).build();
        MetaInfo thriller  = MetaInfo.builder().name("thriller").type(MetaType.GENRE).build();

        given(metaInfoRepository.findByNameAndType("톰 크루즈", MetaType.ACTOR)).willReturn(actor);
        given(metaInfoRepository.findByNameAndType("딘 데블로이스", MetaType.DIRECTOR)).willReturn(director);
        given(metaInfoRepository.findByNameAndType("KR", MetaType.COUNTRY)).willReturn(country);
        given(metaInfoRepository.findByNameAndType("thriller", MetaType.GENRE)).willReturn(thriller);

        given(contentRepository.save(any(Content.class)))
                .willAnswer(inv -> {
                    return ContentFixture.createContent(1L);
                });

        given(metaInfoContentsRepository.save(any(MetaInfoContents.class)))
                .willAnswer(inv -> inv.getArgument(0));

        AdminAddContentRequestDto request = AdminDtoFixture.getAdminAddContentRequestDto();

        // when
        Response<AdminAddContentResponseDto> response = adminContentService.addContent(request);

        // then
        Long contentId = response.content().contentId();
        Assertions.assertNotNull(contentId);
        Assertions.assertNotNull(contentRepository.findById(contentId));
        Assertions.assertNotNull(metaInfoContentsRepository.findByContentId(contentId));
    }

    @Test
    @DisplayName("컨텐츠 추가 - 잘못된 장르 전달 시 FastAPI 서버 예외 전파")
    void contentAddInvalidGenreName_test() {
        given(fastApiClient.vectorFromGenres(List.of("thriller")))
                .willThrow(new IllegalArgumentException("unknown genre"));

        AdminAddContentRequestDto req = AdminDtoFixture.getAdminAddContentRequestDto();
        assertThrows(IllegalArgumentException.class, () -> adminContentService.addContent(req));

        verify(fastApiClient).vectorFromGenres(List.of("thriller"));
    }

    @Test
    @Transactional
    @DisplayName("컨텐츠 수정 - 컨텐츠가 정상적으로 수정됩니다.")
    void updateContent_success() {
        // given
        given(fastApiClient.vectorFromGenres(List.of("액션"))).willReturn(new FastApiVectorFromGenresDto("MOCK_VEC_액션"));

        MetaInfo genre = MetaInfo.builder().name("액션").type(MetaType.GENRE).build();
        MetaInfo actor = MetaInfo.builder().name("톰 크루즈").type( MetaType.ACTOR).build();
        MetaInfo director = MetaInfo.builder().name("딘 데블로이스").type(MetaType.DIRECTOR).build();

        given(metaInfoRepository.findByNameAndType("액션", MetaType.GENRE)).willReturn(genre);
        given(metaInfoRepository.findByNameAndType("톰 크루즈", MetaType.ACTOR)).willReturn(actor);
        given(metaInfoRepository.findByNameAndType("딘 데블로이스", MetaType.DIRECTOR)).willReturn(director);

        Content content = ContentFixture.createContent(1L);

        given(contentRepository.findById(any(Long.class)))
                .willReturn(Optional.of(ContentFixture.createContent(1L)));

        given(contentVectorRepository.findById(any(Long.class)))
                .willReturn(Optional.of(ContentFixture.createContentVector(content, "MOCK_VEC")));

        AdminUpdateContentRequestDto request = AdminDtoFixture.getValidAdminUpdateContentRequestDto();

        // when
        Response<AdminUpdateContentResponseDto> response = adminContentService.updateContent(request);
        ArgumentCaptor<MetaInfoContents> captor = ArgumentCaptor.forClass(MetaInfoContents.class);

        // then
        content = contentRepository.findById(request.contentId()).orElse(null);
        Long contentId = response.content().contentId();
        Assertions.assertNotNull(contentId);
        Assertions.assertNotNull(content);
        Assertions.assertEquals(content.getTitle(), "test-title");
        Assertions.assertEquals(content.getDescription(), "test-desc");
        Assertions.assertEquals(content.getVideoUrl(), "test-videourl");
        Assertions.assertEquals(content.getPostUrl(), "test-posturl");
        Assertions.assertEquals(content.getOpenDate(), LocalDate.parse("2025-01-01").atStartOfDay());
        Assertions.assertNotNull(metaInfoContentsRepository.findByContentId(contentId));
    }

    @Test
    @DisplayName("컨텐츠 수정 - 잘못된 장르 전달 시 FastAPI 서버 예외 전파")
    void contentUpdateInvalidGenreName_test() {

        AdminUpdateContentRequestDto req = AdminDtoFixture.getInvalidAdminUpdateContentRequestDto();
        assertThrows(BusinessException.class, () -> adminContentService.updateContent(req));
    }
}