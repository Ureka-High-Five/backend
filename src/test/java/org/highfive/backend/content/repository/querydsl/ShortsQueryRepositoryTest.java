package org.highfive.backend.content.repository.querydsl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import jakarta.persistence.EntityManager;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.config.TestQuerydslConfig;
import org.highfive.backend.shorts.dto.response.ShortsItemDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.shorts.repository.querydsl.ShortsQueryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ShortsQueryRepository.class, TestQuerydslConfig.class})
class ShortsQueryRepositoryTest {

    @Autowired
    ShortsQueryRepository shortsQueryRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        Content c1 = ContentFixture.createDefaultContent();
        Content c2 = ContentFixture.createDefaultContent();
        em.persist(c1);
        em.persist(c2);

        em.persist(ShortsFixture.createShorts(c1));
        em.persist(ShortsFixture.createShorts(c1));
        em.persist(ShortsFixture.createShorts(c1));
        em.persist(ShortsFixture.createShorts(c1));
        em.persist(ShortsFixture.createShorts(c2));
        em.persist(ShortsFixture.createShorts(c2));
        em.persist(ShortsFixture.createShorts(c2));
        em.persist(ShortsFixture.createShorts(c2));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("쇼츠 개수로 조회 결과 원하는 개수만큼 조회되고, 아이디는 오름차순으로 조회된다.")
    void findByCursor_sizeAndIdTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor(null, 5);
        assertThat(result.items()).hasSize(5)
                .extracting(ShortsItemDto::shortsId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    @DisplayName("쇼츠 커서로 조회 결과 커서 이후의 쇼츠가 조회된다.")
    void findByCursor_cursorTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor("3", 5);
        assertThat(result.items()).hasSize(5)
                .extracting(ShortsItemDto::shortsId)
                .containsExactly(3L, 4L, 5L, 6L, 7L);
    }

    @Test
    @DisplayName("hasNext = true가 올바르게 설정된다.")
    void findByCursor_hasNextTrueTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor("3", 5);
        assertThat(result.hasNext()).isEqualTo(true);
    }

    @Test
    @DisplayName("hasNext = false가 올바르게 설정된다.")
    void findByCursor_hasNextFalseTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor("6", 5);
        assertThat(result.hasNext()).isEqualTo(false);
    }

    @Test
    @DisplayName("cursor가 쇼츠 아이디 범위보다 큰 경우 빈 목록을 반환합니다.")
    void findByCursor_explicitCursorTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor("999", 5);
        assertThat(result.items()).isEmpty();
    }

    @Test
    @DisplayName("cursor가 쇼츠 아이디 범위보다 작은 경우 작은 아이디부터 개수만큼 반환합니다.")
    void findByCursor_negativeCursorTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor("-1", 5);
        assertThat(result.items()).hasSize(5);
    }

    @Test
    @DisplayName("nextCursor가 올바르게 설정됩니다.")
    void findByCursor_nextCursorTest() {
        CursorPageResponse<ShortsItemDto> result = shortsQueryRepository.findByCursor("1", 5);
        assertThat(result.nextCursor()).isEqualTo("6");
    }

    @AfterEach
    void resetAutoIncrement() {
        em.flush();
        em.clear();
        em.createNativeQuery("ALTER TABLE shorts ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();
    }
}