package org.highfive.backend.content.integration;

import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.dto.request.ShortsDislikeRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.shorts.service.ShortsService;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    private Shorts testShorts;
    private List<User> testUsers = new ArrayList<>();

    private static final int NUMBER_OF_THREADS = 50;

    @BeforeEach
    void setup() {
        testShorts = shortsRepository.save(Shorts.builder()
                .shortsUrl("https://example.com/video.mp4")
                .thumbnailUrl("https://example.com/thumb.jpg")
                .content(null)  // 필요시 Content 연결
                .likeCount(0)
                .build());

        // 미리 50명의 유저 생성
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            User user = userRepository.save(User.builder()
                    .name("User " + i)
                    .kakaoUserId("test-user-" + i)
                    .userRole(UserRole.USER)
                    .profileUrl("https://example.com/profile.jpg")
                    .build());
            testUsers.add(user);
        }
    }

    @Test
    @DisplayName("좋아요 동시 요청 동시성 문제가 발생하지 않습니다.")
    void like_concurrency_test() throws InterruptedException {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int userIndex = i;

            executorService.execute(() -> {
                try {
                    User user = testUsers.get(userIndex);
                    ShortsLikeRequestDto dto = new ShortsLikeRequestDto(testShorts.getId(), 123L);
                    shortsService.like(user, dto);
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Shorts result = shortsRepository.findById(testShorts.getId()).orElseThrow();
        assertEquals(NUMBER_OF_THREADS, result.getLikeCount());
        assertEquals(NUMBER_OF_THREADS, shortsLikeTimeLogRepository.count());
    }

    @Test
    @DisplayName("좋아요 취소 동시 요청 동시성 문제가 발생하지 않습니다.")
    void dislike_concurrency_test() throws InterruptedException {

            // given
        for (User user : testUsers) {
            ShortsLikeRequestDto likeDto = new ShortsLikeRequestDto(testShorts.getId(), 123L);
            shortsService.like(user, likeDto);
        }

        assertEquals(NUMBER_OF_THREADS, shortsRepository.findById(testShorts.getId()).get().getLikeCount());
        assertEquals(NUMBER_OF_THREADS, shortsLikeTimeLogRepository.count());

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int userIndex = i;
            executorService.execute(() -> {
                try {
                    User user = testUsers.get(userIndex);
                    ShortsDislikeRequestDto dto = new ShortsDislikeRequestDto(testShorts.getId());
                    shortsService.dislike(user, dto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Shorts result = shortsRepository.findById(testShorts.getId()).orElseThrow();
        assertEquals(0, result.getLikeCount());
        assertEquals(0, shortsLikeTimeLogRepository.count());
    }

    @Test
    @DisplayName("좋아요가 0개일 때 dislike 요청 시 likeCount는 음수가 되지 않는다")
    void dislike_when_likeCount_is_zero() {
        // given
        assertEquals(0, shortsRepository.findById(testShorts.getId()).get().getLikeCount());

        // when
        ShortsDislikeRequestDto dto = new ShortsDislikeRequestDto(testShorts.getId());

        assertThrows(BusinessException.class, () -> {
            shortsService.dislike(testUsers.get(0), dto);
        });

        Shorts result = shortsRepository.findById(testShorts.getId()).orElseThrow();

        // then
        assertEquals(0, result.getLikeCount());
    }
}