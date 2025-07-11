package org.highfive.backend.content.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.content.entity.Content;
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

    @Test
    @DisplayName("존재하는 ID면 content를 반환한다")
    void findById_existedID() {

        //given
        Content content = ContentFixture.creatContent(null);
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
