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

    @AfterEach
    void resetAutoIncrement() {
        em.flush();
        em.clear();
        em.createNativeQuery("ALTER TABLE shorts ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();
    }
}