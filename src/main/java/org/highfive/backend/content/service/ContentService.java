package org.highfive.backend.content.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.QueryDslContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiRecommendResponseDto;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.highfive.backend.content.dto.mapper.ContentMapper.toSearchContentResponseDto;
import static org.highfive.backend.global.code.SuccessCode.OK;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int INIT_CONTENT_CNT = 6;
    private final String LIKE = "%";

    private final ContentRepository contentRepository;
    private final FastApiClient fastApiClient;
    private final PreferMetaInfoRepository preferMetaInfoRepository;
    private final QueryDslContentRepository queryDslContentRepository;

    public List<OnboardingInitContentsResponseDto> getDistinctGenreTopContents() {
        List<MostPopularContentPerGenreDto> topContents = contentRepository.findTopContentPerGenre(INIT_CONTENT_CNT);

        if (topContents.isEmpty()) {
            throw new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND);
        }
        return topContents.stream()
                .map(dto -> new OnboardingInitContentsResponseDto(dto.id(), dto.thumbnailUrl(), dto.title(), dto.openYear()))
                .toList();
    }

    public Response<ContentDetailResponseDto> getContentDetail(final Long contentId) {

        final Content content = queryDslContentRepository.findWithMetaInfoById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));

        final Map<MetaType, List<String>> metaMap = extractMetaInfoMap(content);
        final String director = extractDirector(metaMap);
        final List<String> actors = metaMap.getOrDefault(MetaType.ACTOR, List.of());
        final List<String> genres = metaMap.getOrDefault(MetaType.GENRE, List.of());

        final ContentDetailResponseDto response = ContentMapper.toContentDetailResponseDto(
                content, director, actors, genres
        );

        return new Response<>(OK.getCode(), response, null);
    }

    public Response<CursorPageResponse<SearchContentResponseDto>> search(final String input, final String cursor, final int size) {
        final String keyword = LIKE + input.toLowerCase() + LIKE;

        final List<Content> contents = contentRepository.searchByInput(keyword, cursor, Pageable.ofSize(size));

        if (contents.isEmpty()) {
            throw new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND);
        }

        final Long nextCursor = contents.get(contents.size() - 1).getId();
        return new Response<>(OK.getCode(), toSearchContentResponseDto(contents, nextCursor), OK.getMessage());
    }

    private String extractDirector(final Map<MetaType, List<String>> metaMap) {
        return metaMap.getOrDefault(MetaType.DIRECTOR, List.of())
                .stream()
                .findFirst()
                .orElse(null);
    }

    public MainRecommendDto recommendMainContentsByUser(User user) {
        List<FastApiRecommendResponseDto> contentsByUserVector = fastApiVectorRecommend(
                user.getEmbedding(), 1);
        Content content = contentRepository.findById(contentsByUserVector.getFirst().id())
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
        List<String> genres = getGenres(content);
        return new MainRecommendDto(content.getPostUrl(), content.getDescription(), genres);
    }

    public List<PersonalRecommendDto> recommendContentsByUser(User user, int count) {
        List<FastApiRecommendResponseDto> contentsByUserVector = fastApiVectorRecommend(user.getEmbedding(), count);
        List<PersonalRecommendDto> result = new ArrayList<>();
        for (FastApiRecommendResponseDto dto : contentsByUserVector) {
            long contentId = dto.id();
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
            // 썸네일 url이 아직 없기 때문에 포스터 url 임시로 전달
//            String thumbnailUrl = content.getThumbnailUrl();
            String postUrl = content.getPostUrl();
//            PersonalRecommendDto resultDto = new PersonalRecommendDto(contentId, thumbnailUrl);
            PersonalRecommendDto resultDto = new PersonalRecommendDto(contentId, postUrl);
            result.add(resultDto);
        }
        return result;
    }

    public Map<String, List<GenreContentDto>> recommendContentsByUserGenre(User user, int count) {
        List<String> preferGenresByUser = preferMetaInfoRepository.findPreferGenresByUser(user.getId(), count);
        Map<String, List<GenreContentDto>> result = new HashMap<>();
        for (String genre : preferGenresByUser) {
            List<TopContentsByGenreDto> topContentsByGenre = contentRepository.findTopContentsByGenre(genre, 5);
            result.put(genre, topContentsByGenre.stream().map(tc -> new GenreContentDto(tc.contentId(), tc.thumbnailUrl())).toList());
        }
        return result;
    }

    private List<FastApiRecommendResponseDto> fastApiVectorRecommend(String vector, int count) {
        return fastApiClient.getContentsByVector(vector, count);
    }

    private List<String> getGenres(Content content) {
        List<Map<String, Object>> contentGenresByContentIds = contentRepository.findContentGenresByContentIds(
                List.of(content.getId()));
        List<String> genres = new ArrayList<>();
        for (Map<String, Object> genreInfo : contentGenresByContentIds) {
            String genreName = (String) genreInfo.get("genreName");
            genres.add(genreName);
        }
        return genres;
    }

    private Map<MetaType, List<String>> extractMetaInfoMap(Content content) {
        return content.getMetaInfoContents().stream()
                .map(MetaInfoContents::getMetaInfo)
                .collect(Collectors.groupingBy(
                        MetaInfo::getType,
                        Collectors.mapping(MetaInfo::getName, Collectors.toList())
                ));
    }

}
