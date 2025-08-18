package org.highfive.backend.content.service;

import static org.highfive.backend.global.util.VectorUtil.convertUserVector;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.CurationDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.content.repository.querydsl.ContentQueryRepositoryImpl;
import org.highfive.backend.curation.dto.mapper.CurationMapper;
import org.highfive.backend.curation.repository.jpa.CurationRepository;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.metadata.dto.GenreMapper;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.preference.MongoUserWeight;
import org.highfive.backend.user.exception.UserErrorCode;
import org.highfive.backend.user.repository.jdbc.UserVectorRepository;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.highfive.backend.user.repository.mongo.UserWeightRepository;
import org.highfive.backend.user.repository.redis.UserRedisRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class HomeContentService {

    private final UserRedisRepository userRedisRepository;
    private final ContentRepository contentRepository;
    private final UserWeightRepository userWeightRepository;
    private final UserRepository userRepository;
    private final UserVectorRepository userVectorRepository;
    private final CurationRepository curationRepository;
    private final ContentQueryRepositoryImpl contentQueryRepository;

    private final int PAGE_SIZE = 4;
    private final int CONTENTS_PER_GENRE = 5;
    private final int VECTOR_BASED_RECOMMEND_LIMIT = 1;
    private final int PERSON_RECOMMEND_COUNT = 4;
    private final int GENRE_RECOMMEND_PER_GENRE_COUNT = 2;
    private final String GENRE_NAME = "genreName";

    @Transactional
    @WithSpan
    public Response<HomeContentsResponseDto> getHomeContents(final User user) {
        final MainRecommendDto mainRecommend = recommendMainContentsByUser(user);
        final List<PersonalRecommendDto> personalRecommends = recommendContentsByUser(user, PERSON_RECOMMEND_COUNT);
        final Map<String, List<GenreContentDto>> genreRecommends = recommendContentsByUserGenre(user,
                GENRE_RECOMMEND_PER_GENRE_COUNT);

        final PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        final List<CurationDto> curations = CurationMapper.toCurationDto(
                curationRepository.findRandomCurationsExcludeUser(user.getId(), pageRequest));

        final HomeContentsResponseDto result = new HomeContentsResponseDto(mainRecommend, personalRecommends,
                genreRecommends, curations);
        return new Response<>(SuccessCode.OK.getCode(), result, null);
    }

    private MainRecommendDto recommendMainContentsByUser(final User user) {
        final List<Content> contentsByUserVector = recommendContentsByVector(user, VECTOR_BASED_RECOMMEND_LIMIT);
        final Content content = contentsByUserVector.getFirst();
        final List<String> genres = getGenres(content);
        return new MainRecommendDto(content.getId(), content.getPostUrl(), content.getDescription(), genres,
                content.getTitle(), content.getVideoUrl());
    }

    private List<PersonalRecommendDto> recommendContentsByUser(final User user, final int count) {
        return recommendContentsByVector(user, count).stream()
                .map(dto -> new PersonalRecommendDto(dto.getId(), dto.getThumbnailUrl()))
                .toList();
    }

    private List<Content> recommendContentsByVector(final User user, final int count) {
        String rawVector = userRedisRepository.getUserVector(user.getId());
        if(rawVector == null) {
            rawVector = userVectorRepository.findEmbeddingByUserId(user.getId()).orElseThrow(() -> new BusinessException(UserErrorCode.USER_VECTOR_NOT_FOUND));
            userRedisRepository.setUserVector(user.getId(), rawVector);
        }
        final String userVector = convertUserVector(rawVector);
        userRepository.upsertUserVector(user.getId(), userVector);
        return contentRepository.findRecommendedContentsByUser(user.getId(), count);
    }

    private Map<String, List<GenreContentDto>> recommendContentsByUserGenre(final User user, final int count) {
        final Map<String, List<GenreContentDto>> result = new HashMap<>();
        final List<String> preferGenresByUser = userWeightRepository.findTop2Genres(user.getId(), PageRequest.of(0, 2))
                .stream().map(MongoUserWeight::getName).toList();
        for (String genre : preferGenresByUser) {
            List<TopContentsByGenreDto> topContentsByGenre = contentQueryRepository.findTopContentsByGenreRandom(
                    GenreMapper.CONVERT_DB_GENRE.get(genre), CONTENTS_PER_GENRE);
            result.put(GenreMapper.CONVERT_HOME_GENRE.get(genre),
                    topContentsByGenre.stream().map(tc -> new GenreContentDto(tc.contentId(), tc.thumbnailUrl()))
                            .toList());
        }
        return result;
    }

    private List<String> getGenres(final Content content) {
        final List<Map<String, Object>> contentGenresByContentIds = contentRepository.findContentGenresByContentIds(
                List.of(content.getId()));
        final List<String> genres = new ArrayList<>();
        for (Map<String, Object> genreInfo : contentGenresByContentIds) {
            String genreName = (String) genreInfo.get(GENRE_NAME);
            genres.add(genreName);
        }
        return genres;
    }
}
