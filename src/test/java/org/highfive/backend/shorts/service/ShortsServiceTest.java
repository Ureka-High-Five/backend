package org.highfive.backend.shorts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.shorts.dto.ShortsDto;
import org.highfive.backend.shorts.dto.mapper.ShortsMapper;
import org.highfive.backend.shorts.dto.response.ShortsResponseDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.repository.jpa.ShortsCommentRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRedisRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.shorts.repository.querydsl.ShortsCommentQueryRepository;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.highfive.backend.user.repository.redis.UserRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class ShortsServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ShortsCommentRepository shortsCommentRepository;

    @Mock
    ShortsCommentQueryRepository shortsCommentQueryRepository;

    @Mock
    ShortsLikeTimeLogRepository shortsLikeTimeLogRepository;

    @Mock
    ShortsRepository shortsRepository;

    @Mock
    UserRedisRepository userRedisRepository;

    @Mock
    ShortsRedisRepository shortsRedisRepository;

    @InjectMocks
    ShortsService shortsService;

    @Nested
    class RecommendShortsTest {

        @BeforeEach
        void setAuth() {
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
            User principal = TestUserFactory.createSimpleUserWithId(1L);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    principal, "N/A", authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        @Test
        @DisplayName("첫 페이지 요청 시 쇼츠가 size 개수 만큼 정상적으로 반환됩니다.")
        void firstPageTest() {
            int size = 10;
            User testUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<ShortsDto> testShortsDto = TestShortsDtoFactory.createManyShorts(20);

            when(userRedisRepository.getUserVector(testUser.getId())).thenReturn("test vector");
            when(shortsRedisRepository.findShortsIdsByUserId(testUser.getId())).thenReturn(List.of());
            when(shortsRedisRepository.findByCursor(testUser.getId(), null, size + 1)).thenReturn(testShortsDto);
            when(shortsRepository.findRecommendedShortsByUser(List.of(-1L), testUser.getId(), 20)).thenReturn(testShortsDto.subList(0, 5));
            when(shortsRepository.findRandomShortsExcludingContentIds(anyList(), eq(10))).thenReturn(testShortsDto.subList(5, 10));

            Response<CursorPageResponse<ShortsResponseDto>> response = shortsService.recommendShorts(
                    null, size, testUser);

            List<ShortsResponseDto> items = response.content().items();
            assertThat(items.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("두 번째 이상 페이지 요청 시 쇼츠가 size 개수 만큼 정상적으로 반환됩니다.")
        void secondAndBeyondPageTest() {
            int size = 10;
            User testUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<ShortsDto> testShortsDto = TestShortsDtoFactory.createManyShorts(20);

            when(shortsRedisRepository.findByCursor(testUser.getId(), 10L, size + 1)).thenReturn(testShortsDto);

            Response<CursorPageResponse<ShortsResponseDto>> response = shortsService.recommendShorts(
                    10L, size, testUser);

            List<ShortsResponseDto> items = response.content().items();
            assertThat(items.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("첫 페이지와 두 번째 페이지 쇼츠에 중복이 없습니다.")
        void noDuplicatedShorts_inContinuousShortsTest() {

        }
    }


}
