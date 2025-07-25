package org.highfive.backend.content.integration;

import jakarta.persistence.EntityManager;
import org.highfive.backend.BackendApplication;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.metadata.repository.jpa.MetaInfoRepository;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.dto.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = BackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class OnboardingIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    private EntityManager em;
    @Autowired
    private ContentRepository contentRepo;
    @Autowired
    private MetaInfoRepository metaRepo;
    @Autowired
    private MetaInfoContentsRepository micRepo;
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("온보딩 초기 - 성공 통합 테스트")
    void onboardingInitContents() {
        //given
        insertInitSampleContents();

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

    @Test
    @DisplayName("온보딩 선택 - recommendedIds에 포함된 콘텐츠는 제외된다")
    void onboardingSelectContents_Success() {
        // given
        SampleIds ids = insertSelectSampleContents();

        OnboardingSelectContentRequestDto req = new OnboardingSelectContentRequestDto(
                ids.selectedIds(),
                ids.recommendedIds()  // 제외할 콘텐츠 ID
        );

        String url = "http://localhost:" + port + "/content/recommend";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OnboardingSelectContentRequestDto> entity = new HttpEntity<>(req, headers);

        // when
        ResponseEntity<Response<List<OnboardingSelectContentResponseDto>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Response<List<OnboardingSelectContentResponseDto>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.code()).isEqualTo(20400);

        List<Long> recommendedIds = ids.recommendedIds();
        List<Long> returnedIds = body.content().stream()
                .map(OnboardingSelectContentResponseDto::contentId)
                .toList();

        assertThat(returnedIds).doesNotContainAnyElementsOf(recommendedIds);
    }

    @Test
    @DisplayName("온보딩 선택 - 빈 리스트 통합 테스트")
    void onboardingSelectContents_Empty() {
        OnboardingSelectContentRequestDto req = new OnboardingSelectContentRequestDto(List.of(), List.of());

        String url = "http://localhost:" + port + "/content/recommend";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OnboardingSelectContentRequestDto> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Response<List<OnboardingSelectContentResponseDto>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response<List<OnboardingSelectContentResponseDto>> body = response.getBody();
        assertThat(body.content()).hasSize(0);
        assertThat(body.code()).isEqualTo(20400);
    }

    private SampleIds insertSelectSampleContents() {
        MetaInfo action = metaRepo.save(new MetaInfo(null, "Action", MetaType.GENRE, null));
        MetaInfo drama  = metaRepo.save(new MetaInfo(null, "Drama",  MetaType.GENRE, null));
        metaRepo.save(action);
        metaRepo.save(drama);

        Content cSel1 = contentRepo.save(ContentFixture.createContent(null));
        Content cSel2 = contentRepo.save(ContentFixture.createContent(null));
        Content cSel3 = contentRepo.save(ContentFixture.createContent(null));

        micRepo.saveAll(List.of(
                new MetaInfoContents(null, action, cSel1),
                new MetaInfoContents(null, drama,  cSel1),
                new MetaInfoContents(null, action, cSel2),
                new MetaInfoContents(null, drama,  cSel3)
        ));

        Content cRec1 = contentRepo.save(ContentFixture.createContent(null));
        Content cRec2 = contentRepo.save(ContentFixture.createContent(null));
        Content cRec3 = contentRepo.save(ContentFixture.createContent(null));

        micRepo.saveAll(List.of(
                new MetaInfoContents(null, action, cRec1),
                new MetaInfoContents(null, drama , cRec1),
                new MetaInfoContents(null, action, cRec2),
                new MetaInfoContents(null, drama , cRec3)
        ));

        return new SampleIds(
                List.of(cSel1.getId(), cSel2.getId(), cSel3.getId()),
                List.of(cRec1.getId(), cRec2.getId(), cRec3.getId())
        );
    }

    private record SampleIds(List<Long> selectedIds, List<Long> recommendedIds) {}

    private void insertInitSampleContents() {
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
