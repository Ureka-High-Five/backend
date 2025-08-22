package org.highfive.backend.content.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.MetaInfoContentsFixture;
import org.highfive.backend.common.fixture.MetaInfoFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.content.dto.ContentDetailDto;
import org.highfive.backend.content.dto.MetaInfoDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.querydsl.ContentQueryRepositoryImpl;
import org.highfive.backend.global.code.SuccessCode;
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

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {

    @Mock
    private ContentQueryRepositoryImpl contentQueryRepositoryImpl;

    @InjectMocks
    private ContentService contentService;

    @Test
    @DisplayName("존재하는 content일 경우 content의 상세 정보를 반환한다")
    public void getContentDetail_correctContentId() {
        // given
        long contentId = 1L;

        // ContentDetailDto (서비스가 기대하는 DTO)
        ContentDetailDto contentDetailDto = new ContentDetailDto(
                "테스트 제목",
                120,            // runningTime
                15,             // grade
                "https://post", // postUrl
                LocalDateTime.of(2025, 1, 1, 0, 0), // openDate
                "설명입니다",    // description
                123L,           // shortsId (없을 수 있으면 Long 권장)
                "https://video" // videoUrl
        );

        // MetaInfoDto (서비스가 기대하는 DTO)
        MetaInfoDto metaInfoDto = new MetaInfoDto(
                List.of("로맨스"),              // genres
                List.of("배우1", "배우2"),      // actors
                "감독"                          // director
        );

        when(contentQueryRepositoryImpl.findContentDetailById(contentId))
                .thenReturn(Optional.of(contentDetailDto));
        when(contentQueryRepositoryImpl.findMetaInfoById(contentId))
                .thenReturn(Optional.of(metaInfoDto));

        // when
        Response<ContentDetailResponseDto> response = contentService.getContentDetailById(contentId);

        // then
        assertEquals(SuccessCode.OK.getCode(), response.code());
        var result = response.content();
        assertNotNull(result);
        assertEquals("테스트 제목", result.contentTitle());
        assertEquals(2025, result.openYear());
        assertEquals(List.of("배우1", "배우2"), result.actors());
        assertEquals(List.of("로맨스"), result.contentGenres());
        assertEquals("감독", result.director());

    }

    @Test
    @DisplayName("컨텐츠가 없으면 예외를 던진다")
    void getContentDetail_noContentId() {
        // given
        long contentId = 93498579L;
        when(contentQueryRepositoryImpl.findContentDetailById(contentId))
                .thenReturn(Optional.empty());

        // when
        BusinessException ex = assertThrows(BusinessException.class,
                () -> contentService.getContentDetailById(contentId));

        // then
        assertEquals(ContentErrorCode.CONTENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("메타 정보가 없으면 예외를 던진다")
    void getContentDetail_noMeta() {
        long contentId = 1L;

        // 컨텐츠 기본 정보는 존재
        var contentDetailDto = new ContentDetailDto(
                "제목", 120, 15, "https://post",
                LocalDateTime.of(2025,1,1,0,0),
                "설명", 123L, "https://video"
        );
        when(contentQueryRepositoryImpl.findContentDetailById(contentId))
                .thenReturn(Optional.of(contentDetailDto));

        when(contentQueryRepositoryImpl.findMetaInfoById(contentId))
                .thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> contentService.getContentDetailById(contentId));

        assertEquals(ContentErrorCode.CONTENT_META_INFO_NOT_FOUND, ex.getErrorCode());
    }
}