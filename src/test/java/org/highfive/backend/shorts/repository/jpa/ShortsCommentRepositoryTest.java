package org.highfive.backend.shorts.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.config.TestQuerydslConfig;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.config.JpaConfig;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({TestQuerydslConfig.class, JpaConfig.class})
class ShortsCommentRepositoryTest {

    @Autowired
    private ShortsCommentRepository shortsCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShortsRepository shortsRepository;

    @Autowired
    private ContentRepository contentRepository;

    User testUser;
    Shorts savedShorts;

    @BeforeEach
    void beforeEach() {
        testUser = UserFixture.createDefaultUser();
        userRepository.save(testUser);
        Content content = ContentFixture.createDefaultContent();
        contentRepository.save(content);
        Shorts shorts = ShortsFixture.createShorts(content);
        savedShorts = shortsRepository.save(shorts);
    }

    @Test
    @DisplayName("특정 shortsId와 time에 해당하는 댓글들을 최신순으로 조회한다")
    void findByShortsIdAndTimeOrderByCreatedAtDesc_test() throws InterruptedException {
        shortsCommentRepository.save(ShortsComment.of(testUser, savedShorts, "comment1", 5L));
        Thread.sleep(2);
        shortsCommentRepository.save(ShortsComment.of(testUser, savedShorts, "comment2", 5L));
        Thread.sleep(2);
        shortsCommentRepository.save(ShortsComment.of(testUser, savedShorts, "comment3", 5L));

        List<ShortsComment> result = shortsCommentRepository.findByShortsIdAndTimeOrderByCreatedAtDesc(savedShorts.getId(), 5L);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.getFirst().getMessage()).isEqualTo("comment3");
    }

    @Test
    @DisplayName("특정 time의 댓글이 없는 경우 조회하지 않는다.")
    void findByShortsIdAndTimeOrderByCreatedAtDesc_emptyTest() {
        shortsCommentRepository.save(ShortsComment.of(testUser, savedShorts, "comment1", 5L));
        shortsCommentRepository.save(ShortsComment.of(testUser, savedShorts, "comment2", 5L));
        shortsCommentRepository.save(ShortsComment.of(testUser, savedShorts, "comment3", 5L));

        List<ShortsComment> result = shortsCommentRepository.findByShortsIdAndTimeOrderByCreatedAtDesc(savedShorts.getId(), 6L);

        // then
        assertThat(result.size()).isEqualTo(0);
    }
}