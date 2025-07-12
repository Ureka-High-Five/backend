package org.highfive.backend.content.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.entity.repository.MetaInfoRepository;
import org.highfive.backend.global.config.QueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
public class ContentRepositoryTest {

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private MetaInfoRepository metaInfoRepository;
    @Autowired
    private MetaInfoContentsRepository micRepository;

    @DisplayName("온보딩 초기 - 장르별 인기 1위 콘텐츠 6개 조회")
    @Test
    void findTopContentPerGenre() {
        // given
        MetaInfo action = metaInfoRepository.save(new MetaInfo(null, "Action", MetaType.GENRE, null));
        MetaInfo comedy = metaInfoRepository.save(new MetaInfo(null, "Comedy", MetaType.GENRE, null));
        MetaInfo thriller = metaInfoRepository.save(new MetaInfo(null, "Thriller", MetaType.GENRE, null));
        MetaInfo horror = metaInfoRepository.save(new MetaInfo(null, "Horror", MetaType.GENRE, null));
        MetaInfo sf = metaInfoRepository.save(new MetaInfo(null, "SF", MetaType.GENRE, null));
        MetaInfo drama = metaInfoRepository.save(new MetaInfo(null, "DRAMA", MetaType.GENRE, null));
        MetaInfo kids = metaInfoRepository.save(new MetaInfo(null, "KIDS", MetaType.GENRE, null));

        Content c1 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c1", 10));
        Content c2 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c2",20));
        Content c3 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c3",30));
        Content c4 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c4",40));
        Content c5 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c5",50));
        Content c6 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c6",60));
        Content c7 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c7",70));
        Content c8 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c8",80));
        Content c9 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c9",90));
        Content c10 = contentRepository.save(ContentFixture.createContentByPopularity(null, "c10",100));

        micRepository.save(new MetaInfoContents(null, action, c1));
        micRepository.save(new MetaInfoContents(null, action, c2));
        micRepository.save(new MetaInfoContents(null, comedy, c3));
        micRepository.save(new MetaInfoContents(null, comedy, c4));
        micRepository.save(new MetaInfoContents(null, thriller, c5));
        micRepository.save(new MetaInfoContents(null, thriller, c6));
        micRepository.save(new MetaInfoContents(null, horror, c7));
        micRepository.save(new MetaInfoContents(null, sf, c8));
        micRepository.save(new MetaInfoContents(null, drama, c9));
        micRepository.save(new MetaInfoContents(null, kids, c10));

        // when
        List<MostPopularContentPerGenreDto> result =
                contentRepository.findTopContentPerGenre(6);

        // then
        assertThat(result)
                .hasSize(6)
                .extracting("id", "title")
                .containsExactlyInAnyOrder(
                        tuple(c4.getId(), "c4"),    // comedy 1위
                        tuple(c6.getId(), "c6"),    // thriller 1위
                        tuple(c7.getId(), "c7"),    // horror 1위
                        tuple(c8.getId(), "c8"),    // sf 1위
                        tuple(c9.getId(), "c9"),    // drama 1위
                        tuple(c10.getId(), "c10")   // kids 1위
                );
    }

    @Test
    @DisplayName("존재하는 ID면 content를 반환한다")
    void findById_existedID() {

        //given
        Content content = ContentFixture.createContent(null);
        Content saved = contentRepository.save(content);

        // when
        Optional<Content> result = contentRepository.findById(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo(saved.getTitle());

    }

    @Test
    @DisplayName("존재하지 않는 content id이면 빈 값을 반환한다")
    void findById_NoContent() {
        // when
        Optional<Content> result = contentRepository.findById(-1L);

        // then
        assertThat(result).isEmpty();
    }
}
