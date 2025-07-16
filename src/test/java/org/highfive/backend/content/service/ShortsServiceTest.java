package org.highfive.backend.content.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.shorts.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsAndLikedItemDto;
import org.highfive.backend.shorts.dto.response.ShortsItemDto;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.querydsl.ShortsQueryRepository;
import org.highfive.backend.shorts.service.ShortsService;
import org.highfive.backend.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShortsServiceTest {

    @Mock
    ShortsQueryRepository queryRepo;

    @Mock
    ShortsLikeTimeLogRepository likeTimeLogRepo;

    @InjectMocks
    ShortsService shortsService;

    private final User testUser = UserFixture.createEmbeddingUser();

    @Test
    @DisplayName("쇼츠에 자신의 좋아요 여부가 표시된다")
    void recommendShorts_likedFlagTest() {
        // given
        ShortsItemDto item1 = new ShortsItemDto(10L, "콘텐츠A", 100L, "url1");
        ShortsItemDto item2 = new ShortsItemDto(20L, "콘텐츠B", 200L, "url2");

        CursorPageResponse<ShortsItemDto> page = new CursorPageResponse<>(List.of(item1, item2), false, null);
        given(queryRepo.findByCursor(any(), eq(5))).willReturn(page);

        given(likeTimeLogRepo.existsByUserIdAndShortsId(testUser.getId(), 100L)).willReturn(true);
        given(likeTimeLogRepo.existsByUserIdAndShortsId(testUser.getId(), 200L)).willReturn(false);

        // when
        Response<RecommendShortsResponseDto> resp = shortsService.recommendShorts(null, 5, testUser);

        // then
        RecommendShortsResponseDto dto = resp.content();
        assertThat(dto.videoType()).isEqualTo(VideoType.SHORTS.name());

        List<ShortsAndLikedItemDto> items = dto.shorts().items();
        assertThat(items.size()).isEqualTo(2);

        ShortsAndLikedItemDto likedItem = items.get(0);
        ShortsAndLikedItemDto notLikedItem = items.get(1);

        assertThat(likedItem.shortsId()).isEqualTo(100L);
        assertThat(likedItem.liked()).isTrue();

        assertThat(notLikedItem.shortsId()).isEqualTo(200L);
        assertThat(notLikedItem.liked()).isFalse();
    }
}
