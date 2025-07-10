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
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiRecommendResponseDto;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int INIT_CONTENT_CNT = 6;

    private final ContentRepository contentRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;
    private final FastApiClient fastApiClient;
    private final PreferMetaInfoRepository preferMetaInfoRepository;

    public List<OnboardingInitContentsResponseDto> getDistinctGenreTopContents() {
        List<MostPopularContentPerGenreDto> topContents = contentRepository.findTopContentPerGenre(INIT_CONTENT_CNT);

        return topContents.stream()
                .map(dto -> new OnboardingInitContentsResponseDto(dto.getId(), dto.getThumbnailUrl(), dto.getTitle()))
                .toList();
    }

    public Response<ContentDetailResponseDto> getContentDetail(final Long contentId, final User user) {

        Content existedContent = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));

        List<MetaInfoContents> infoContentsList = metaInfoContentsRepository.findByContent_Id(contentId);

        List<MetaInfo> metaInfos = infoContentsList.stream()
                .map(MetaInfoContents::getMetaInfo)
                .toList();

        Map<MetaType, List<String>> metaMap = metaInfos.stream()
                .collect(Collectors.groupingBy(
                        MetaInfo::getType,
                        Collectors.mapping(MetaInfo::getName, Collectors.toList())
                ));

        String director = metaMap.getOrDefault(MetaType.DIRECTOR, List.of())
                .stream().findFirst().orElse(null);

        List<String> actors = metaMap.getOrDefault(MetaType.ACTOR, List.of());
        List<String> genres = metaMap.getOrDefault(MetaType.GENRE, List.of());

        ContentDetailResponseDto response = ContentMapper.toContentDetailResponseDto(existedContent, director, actors,
                genres);

        return new Response<>(SuccessCode.OK.getCode(), response, null);
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
            String thumbnailUrl = content.getThumbnailUrl();
            PersonalRecommendDto resultDto = new PersonalRecommendDto(contentId, thumbnailUrl);
            result.add(resultDto);
        }
        return result;
    }

    public Map<String, List<GenreContentDto>> recommendContentsByUserGenre(User user, int count) {
        List<String> preferGenresByUser = preferMetaInfoRepository.findPreferGenresByUser(user.getId(), 2);
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
            String genreName = (String) genreInfo.get(content.getId());
            genres.add(genreName);
        }
        return genres;
    }
}
