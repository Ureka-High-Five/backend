package org.highfive.backend.content.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.MetaInfoContentsFixture;
import org.highfive.backend.common.fixture.MetaInfoFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.ContentVideoResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.content.repository.querydsl.ContentQueryRepositoryImpl;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.shorts.entity.Shorts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {

    @Mock
    private ContentQueryRepositoryImpl contentQueryRepositoryImpl;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

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

        Content contentWithMeta = ContentFixture.createContentWithMetaInfo(content, metaInfoContents);

        Shorts shorts = ShortsFixture.createShorts(contentWithMeta);
        contentWithMeta.getShorts().add(shorts);

        when(contentQueryRepositoryImpl.findWithMetaInfoById(contentId)).thenReturn(Optional.of(contentWithMeta));

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
        when(contentQueryRepositoryImpl.findWithMetaInfoById(contentId)).thenReturn(Optional.empty());

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contentService.getContentDetail(contentId);
        });

        //then
        assertEquals(ContentErrorCode.CONTENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("컨텐츠 페이지네이션 - size개의 컨텐츠가 조회된다.")
    public void content_pagination_correctly() {

        //given
        int size = 3;

        List<Content> contents = List.of(
                ContentFixture.createContent(10L),
                ContentFixture.createContent(11L),
                ContentFixture.createContent(12L),
                ContentFixture.createContent(13L)
        );

        when(contentRepository.searchByInput(anyString(), any(), any(Pageable.class)))
                .thenReturn(contents);

        //when
        Response<CursorPageResponse<SearchContentResponseDto>> response = contentService.search("범죄도시", null, size);

        //then
        CursorPageResponse<SearchContentResponseDto> page = response.content();
        assertTrue(page.hasNext());
        assertEquals(size, page.items().size());
        assertEquals(String.valueOf(12L), page.nextCursor());

    }

    @Test
    @DisplayName("컨텐츠 페이지네이션 - 마지막 페이지이면 size개 이하의 컨텐츠가 조회된다.")
    public void content_pagination_last_page() {
        //given
        int size = 2;

        List<Content> contents = List.of(
                ContentFixture.createContent(100L),
                ContentFixture.createContent(101L)
        );

        when(contentRepository.searchByInput(anyString(), any(), any(Pageable.class)))
                .thenReturn(contents);

        //when
        Response<CursorPageResponse<SearchContentResponseDto>> response = contentService.search("범죄도시", null, size);

        //then
        CursorPageResponse<SearchContentResponseDto> page = response.content();
        assertFalse(page.hasNext());
        assertNull(page.nextCursor());
        assertEquals(2, page.items().size());
    }

    @Test
    @DisplayName("컨텐츠에 해당하는 videoUrl을 반환한다.")
    public void getContentVideo_success() {
        //given
        Long contentId = 200L;
        String expectedVideoUrl = "s3://video.mp4";
        Content content = ContentFixture.createContent(contentId);
        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));

        //when
        Response<ContentVideoResponseDto> response = contentService.getContentVideo(contentId);

        //then
        assertEquals(expectedVideoUrl, response.content().videoUrl());
    }

    @Test
    @DisplayName("컨텐츠가 존재하지 않으면 CONTENT_NOT_FOUND 예외가 발생한다.")
    void getContentVideo_notFound() {
        // given
        Long contentId = 99999L;
        when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> contentService.getContentVideo(contentId));

        // then
        assertEquals(ContentErrorCode.CONTENT_NOT_FOUND, exception.getErrorCode());
    }

}