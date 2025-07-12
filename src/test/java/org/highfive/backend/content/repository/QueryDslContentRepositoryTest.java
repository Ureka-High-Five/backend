package org.highfive.backend.content.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.QueryDslContentRepositoryTest.QueryDslTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({QueryDslContentRepository.class, QueryDslTestConfig.class})
@Transactional
class QueryDslContentRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private QueryDslContentRepository queryDslContentRepository;

    @TestConfiguration
    static class QueryDslTestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Test
    @DisplayName("온보딩 컨텐츠 선택 - 선택한 컨텐츠들의 장르 중 가장 많은 장르 2개를 골라낸다")
    void findTopGenresByContentIds_returnsTopTwoGenres() {
        //given
        MetaInfo action = new MetaInfo(null, "Action", MetaType.GENRE, null);
        MetaInfo comedy = new MetaInfo(null, "Comedy", MetaType.GENRE, null);
        MetaInfo drama  = new MetaInfo(null, "Drama",  MetaType.GENRE, null);
        em.persist(action);
        em.persist(comedy);
        em.persist(drama);

        Content c1 = ContentFixture.createContent(null); em.persist(c1);
        Content c2 = ContentFixture.createContent(null); em.persist(c2);
        Content c3 = ContentFixture.createContent(null); em.persist(c3);

        em.persist(new MetaInfoContents(null, action, c1));
        em.persist(new MetaInfoContents(null, comedy, c1));
        em.persist(new MetaInfoContents(null, action, c2));
        em.persist(new MetaInfoContents(null, drama, c2));
        em.persist(new MetaInfoContents(null, drama, c3));
        em.persist(new MetaInfoContents(null, action, c3));

        em.flush();
        em.clear();

        // when
        List<GenreCountDto> result =
                queryDslContentRepository.findTopGenresByContentIds(List.of(1L, 2L, 3L));

        // then
        assertThat(result).hasSize(2);

        GenreCountDto first  = result.get(0);
        GenreCountDto second = result.get(1);

        assertThat(first.genre()).isEqualTo("Action");
        assertThat(first.cnt()).isEqualTo(3);

        assertThat(second.genre()).isEqualTo("Drama");
        assertThat(second.cnt()).isEqualTo(2);
    }
}