package org.highfive.backend.content.integration;

import org.highfive.backend.content.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.content.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.content.repository.jpa.ShortsRepository;
import org.highfive.backend.content.service.ShortsService;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class ShortsIntegrationTest {

    @Autowired
    private ShortsRepository shortsRepository;

    @Autowired
    private ShortsLikeTimeLogRepository shortsLikeTimeLogRepository;

    @Autowired
    private ShortsService shortsService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Shorts testShorts;

    @BeforeEach
    void setUp() {
        // 테스트 유저 및 쇼츠 데이터 생성
        testUser = userRepository.save(User.builder()
                .name("박상윤")
                .kakaoUserId("kakao-test-user")
                .profileUrl("http://kakao.com/123")
                .userRole(UserRole.USER)
                .build());

        testShorts = shortsRepository.save(Shorts.builder()

                .shortsUrl("https://example.com/video.mp4")
                .thumbnailUrl("https://example.com/thumb.jpg")
                .content(/* 연관 Content 설정 */ null)
                .likeCount(0)
                .build());
    }

    @Test
    @DisplayName("좋아요 동시 요청시 동시성 문제가 발생 하지 않습니다.")
    void shorts_like_concurrency() throws Exception {

        // given
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    ShortsLikeRequestDto dto = new ShortsLikeRequestDto(testShorts.getId(), 120L);
                    shortsService.like(testUser, dto);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Shorts result = shortsRepository.findById(testShorts.getId()).orElseThrow();

        // then
        assertEquals(numberOfThreads, result.getLikeCount());
        assertEquals(numberOfThreads, shortsLikeTimeLogRepository.count());
    }
}