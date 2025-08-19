package org.highfive.backend.shorts.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.shorts.dto.ShortsDto;
import org.highfive.backend.shorts.dto.mapper.ShortsMapper;
import org.highfive.backend.shorts.dto.response.ShortsResponseDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRedisRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.shorts.repository.querydsl.ShortsQueryRepository;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.highfive.backend.user.repository.redis.UserRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShortsServiceTest {

    @Mock
    private ShortsRepository shortsRepository;

    @Mock
    private ShortsRedisRepository shortsRedisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRedisRepository userRedisRepository;

    @Mock
    ShortsQueryRepository queryRepo;

    @Mock
    ShortsLikeTimeLogRepository likeTimeLogRepo;

    @InjectMocks
    ShortsService shortsService;

    private final User testUser = UserFixture.createUser(1L);

    @Test
    @DisplayName("'쇼츠에 자신의 좋아요 여부가 표시된다")
    void recommendShorts_likedFlagTest() {
        // given
        Content content = ContentFixture.createDefaultContent();
        Shorts shorts1 = ShortsFixture.createShortsById(content, 1L);
        Shorts shorts2 = ShortsFixture.createShortsById(content, 2L);

        List<ShortsDto> shortsDtos = List.of(
                ShortsMapper.toShortsDto(shorts1),
                ShortsMapper.toShortsDto(shorts2)
        );

        given(userRedisRepository.getUserVector(testUser.getId())).willReturn("test");

        given(shortsRedisRepository.findByCursor(eq(testUser.getId()), isNull(), eq(6)))
                .willReturn(shortsDtos);

        given(likeTimeLogRepo.findLikedShortsIds(eq(testUser.getId()), anyList()))
                .willReturn(List.of(1L));

        // when
        Response<CursorPageResponse<ShortsResponseDto>> resp = shortsService.recommendShorts(null, 5, testUser);

        // then
        List<ShortsResponseDto> dto = resp.content().items();

        assertThat(dto.size()).isEqualTo(2);

        ShortsResponseDto likedItem = dto.get(0);
        ShortsResponseDto notLikedItem = dto.get(1);

        assertThat(likedItem.shortsId()).isEqualTo(1L);
        assertThat(likedItem.liked()).isTrue();

        assertThat(notLikedItem.shortsId()).isEqualTo(2L);
        assertThat(notLikedItem.liked()).isFalse();
    }
}
