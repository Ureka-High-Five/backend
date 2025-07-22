package org.highfive.backend.content.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiRecommendResponseDto;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.highfive.backend.user.repository.redis.UserRedisRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeContentService {

    private final FastApiClient fastApiClient;
    private final UserRedisRepository userRedisRepository;
    private final ContentRepository contentRepository;
    private final PreferMetaInfoRepository preferMetaInfoRepository;
    private final UserRepository userRepository;

    public Response<HomeContentsResponseDto> getHomeContents(final User user) {
        final MainRecommendDto mainRecommend = recommendMainContentsByUser(user);
        final List<PersonalRecommendDto> personalRecommends = recommendContentsByUser(user, 4);
        final Map<String, List<GenreContentDto>> genreRecommends = recommendContentsByUserGenre(user, 2);

        // todo 사용자가 선호하는 장르 기반 큐레이션 조회(1차 MVP 이후)

        final HomeContentsResponseDto result = new HomeContentsResponseDto(mainRecommend, personalRecommends, genreRecommends, null);
        return new Response<>(SuccessCode.OK.getCode(), result, null);
    }

    private MainRecommendDto recommendMainContentsByUser(final User user) {
        final List<FastApiRecommendResponseDto> contentsByUserVector = recommendContentsByVector(user, 1);
        final Content content = contentRepository.findById(contentsByUserVector.getFirst().id())
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
        final List<String> genres = getGenres(content);
        return new MainRecommendDto(content.getId(), content.getPostUrl(), content.getDescription(), genres, content.getTitle());
    }

    private List<PersonalRecommendDto> recommendContentsByUser(final User user, final int count) {
        return recommendContentsByVector(user, count).stream()
                .map(dto -> {
                    Content content = getContentById(dto.id());
                    return new PersonalRecommendDto(content.getId(), content.getThumbnailUrl());
                })
                .toList();
    }

    private Content getContentById(long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    private List<FastApiRecommendResponseDto> recommendContentsByVector(final User user, final int count) {
        final String userVector = userRedisRepository.getUserVector(user.getId());
        log.info("userId={}, user vector={}", user.getId(), userVector);
        userRepository.upsertUserVector(user.getId(), userVector);
        return fastApiVectorRecommend(userVector, count);
    }

    private Map<String, List<GenreContentDto>> recommendContentsByUserGenre(final User user, final int count) {
        final List<String> preferGenresByUser = preferMetaInfoRepository.findPreferGenresByUser(user.getId(), count);
        final Map<String, List<GenreContentDto>> result = new HashMap<>();
        for (String genre : preferGenresByUser) {
            List<TopContentsByGenreDto> topContentsByGenre = contentRepository.findTopContentsByGenre(genre, 5);
            result.put(genre, topContentsByGenre.stream().map(tc -> new GenreContentDto(tc.contentId(), tc.thumbnailUrl())).toList());
        }
        return result;
    }

    private List<String> getGenres(final Content content) {
        final List<Map<String, Object>> contentGenresByContentIds = contentRepository.findContentGenresByContentIds(List.of(content.getId()));
        final List<String> genres = new ArrayList<>();
        for (Map<String, Object> genreInfo : contentGenresByContentIds) {
            String genreName = (String) genreInfo.get("genreName");
            genres.add(genreName);
        }
        return genres;
    }

    private List<FastApiRecommendResponseDto> fastApiVectorRecommend(final String vector, final int count) {
        return fastApiClient.getContentsByVector(vector, count);
    }
}
