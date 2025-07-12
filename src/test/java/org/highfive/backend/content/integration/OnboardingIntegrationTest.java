package org.highfive.backend.content.integration;

import org.highfive.backend.BackendApplication;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.entity.repository.MetaInfoRepository;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.global.dto.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = BackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class OnboardingIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Test
    @DisplayName("온보딩 초기 - 성공 통합 테스트")
    void onboardingInitContents() {
        //given
        insertSampleContents();

        // when
        String url = "http://localhost:" + port + "/content/init";
        Response<List<OnboardingInitContentsResponseDto>> res = restTemplate.getForObject(url, Response.class);

        // then
        assertThat(res.code()).isEqualTo(20000);
        List<OnboardingInitContentsResponseDto> data = res.content();
        assertThat(data).hasSize(6);
    }

    @Test
    @DisplayName("온보딩 초기 - 컨텐츠가 없는 경우 에러 반환")
    void onboardingInitContents_noData_returns404() {
        // given: DB에 아무 데이터도 넣지 않음

        // when
        String url = "http://localhost:" + port + "/content/init";
        var responseEntity =
                restTemplate.getForEntity(url, Response.class);

        // then
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(404);

        Response<?> body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.code()).isEqualTo(40402);
        assertThat(body.message()).isEqualTo("존재하지 않는 컨텐츠입니다.");
    }

    @Autowired
    private ContentRepository contentRepo;
    @Autowired
    private MetaInfoRepository metaRepo;
    @Autowired
    private MetaInfoContentsRepository micRepo;

    private void insertSampleContents() {
        MetaInfo action   = metaRepo.save(new MetaInfo(null, "Action",   MetaType.GENRE, null));
        MetaInfo comedy   = metaRepo.save(new MetaInfo(null, "Comedy",   MetaType.GENRE, null));
        MetaInfo thriller = metaRepo.save(new MetaInfo(null, "Thriller", MetaType.GENRE, null));
        MetaInfo horror   = metaRepo.save(new MetaInfo(null, "Horror",   MetaType.GENRE, null));
        MetaInfo sf       = metaRepo.save(new MetaInfo(null, "SF",       MetaType.GENRE, null));
        MetaInfo drama    = metaRepo.save(new MetaInfo(null, "Drama",    MetaType.GENRE, null));
        MetaInfo kids     = metaRepo.save(new MetaInfo(null, "Kids",     MetaType.GENRE, null));

        Content c1 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c1", 10));
        micRepo.save(new MetaInfoContents(null, action, c1));
        Content c2 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c2", 20));
        micRepo.save(new MetaInfoContents(null, comedy, c2));
        Content c3 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c3", 30));
        micRepo.save(new MetaInfoContents(null, thriller, c3));
        Content c4 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c4", 40));
        micRepo.save(new MetaInfoContents(null, horror, c4));
        Content c5 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c5", 50));
        micRepo.save(new MetaInfoContents(null, sf, c5));
        Content c6 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c6", 60));
        micRepo.save(new MetaInfoContents(null, drama, c6));
        Content c7 = contentRepo.save(ContentFixture.createContentByPopularity(null, "c7", 70));
        micRepo.save(new MetaInfoContents(null, kids, c7));
    }
}
