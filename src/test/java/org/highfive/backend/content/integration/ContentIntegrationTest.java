package org.highfive.backend.content.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.MetaInfoContentsFixture;
import org.highfive.backend.common.fixture.MetaInfoFixture;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.jpa.MetaInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContentIntegrationTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private MetaInfoContentsRepository metaInfoContentsRepository;

    @Autowired
    private MetaInfoRepository metaInfoRepository;

    @Autowired
    private MockMvc mockMvc;

    private Content savedContent;


    @BeforeEach
    void setUp() {
        Content content = ContentFixture.createContent(null);
        savedContent = contentRepository.save(content);

        MetaInfo director = metaInfoRepository.save(MetaInfoFixture.createMetaInfo("감독", MetaType.DIRECTOR));
        MetaInfo genre1 = metaInfoRepository.save(MetaInfoFixture.createMetaInfo("로맨스", MetaType.GENRE));
        MetaInfo genre2 = metaInfoRepository.save(MetaInfoFixture.createMetaInfo("스릴러", MetaType.GENRE));
        MetaInfo actor1 = metaInfoRepository.save(MetaInfoFixture.createMetaInfo("이도현", MetaType.ACTOR));
        MetaInfo actor2 = metaInfoRepository.save(MetaInfoFixture.createMetaInfo("황민현", MetaType.ACTOR));

        List<MetaInfoContents> metaInfoContents = List.of(
                MetaInfoContentsFixture.createMetaInfoContents(director, savedContent),
                MetaInfoContentsFixture.createMetaInfoContents(genre1, savedContent),
                MetaInfoContentsFixture.createMetaInfoContents(genre2, savedContent),
                MetaInfoContentsFixture.createMetaInfoContents(actor1, savedContent),
                MetaInfoContentsFixture.createMetaInfoContents(actor2, savedContent)
        );

        metaInfoContentsRepository.saveAll(metaInfoContents);

        Content contentWithMeta = ContentFixture.createContentWithMetaInfo(savedContent, metaInfoContents);
        savedContent = contentRepository.save(contentWithMeta);

    }

    @Test
    @WithMockUser
    @DisplayName("콘텐츠 상세 정보를 조회한다")
    public void getContentDetail_totalTest() throws Exception {
        //when
        //then
        mockMvc.perform(get("/content/{contentId}/detail", savedContent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20000))
                .andExpect(jsonPath("$.content.contentTitle").value(savedContent.getTitle()))
                .andExpect(jsonPath("$.content.director").value("감독"))
                .andExpect(jsonPath("$.content.actors[0]").value("이도현"))
                .andExpect(jsonPath("$.content.actors[1]").value("황민현"))
                .andExpect(jsonPath("$.content.contentGenres[0]").value("로맨스"))
                .andExpect(jsonPath("$.content.contentGenres[1]").value("스릴러"));
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠 ID로 요청 시 에러를 반환한다")
    public void getContentDetail_totalTest_fail() throws Exception {
        Long notExistedId = savedContent.getId() + 999L;

        mockMvc.perform(get("/content/{contentId}/detail", notExistedId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(40402))
                .andExpect(jsonPath("$.message").value("존재하지 않는 컨텐츠입니다."));
    }
}
