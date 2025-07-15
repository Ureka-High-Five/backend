package org.highfive.backend.content.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
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
public class HomeContentService {

    private final FastApiClient fastApiClient;
    private final ContentRepository contentRepository;
    private final PreferMetaInfoRepository preferMetaInfoRepository;

    public Response<HomeContentsResponseDto> getHomeContents(User user) {
        MainRecommendDto mainRecommend = recommendMainContentsByUser(user);
        List<PersonalRecommendDto> personalRecommends = recommendContentsByUser(user, 4);
        Map<String, List<GenreContentDto>> genreRecommends = recommendContentsByUserGenre(user, 2);

        // todo 사용자가 선호하는 장르 기반 큐레이션 조회(1차 MVP 이후)

        HomeContentsResponseDto result = new HomeContentsResponseDto(mainRecommend, personalRecommends, genreRecommends, null);
        return new Response<>(SuccessCode.OK.getCode(), result, null);
    }

    private MainRecommendDto recommendMainContentsByUser(User user) {
        List<FastApiRecommendResponseDto> contentsByUserVector = fastApiVectorRecommend(user.getEmbedding(), 1);
        Content content = contentRepository.findById(contentsByUserVector.getFirst().id())
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
        List<String> genres = getGenres(content);
        return new MainRecommendDto(content.getId(), content.getPostUrl(), content.getDescription(), genres);
    }

    private List<PersonalRecommendDto> recommendContentsByUser(User user, int count) {
        List<FastApiRecommendResponseDto> contentsByUserVector = fastApiVectorRecommend(user.getEmbedding(), count);
        List<PersonalRecommendDto> result = new ArrayList<>();
        for (FastApiRecommendResponseDto dto : contentsByUserVector) {
            long contentId = dto.id();
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
            // todo 썸네일 url이 아직 없기 때문에 포스터 url 임시로 전달
            String postUrl = content.getPostUrl();
            PersonalRecommendDto resultDto = new PersonalRecommendDto(contentId, postUrl);
//            String thumbnailUrl = content.getThumbnailUrl();
//            PersonalRecommendDto resultDto = new PersonalRecommendDto(contentId, thumbnailUrl);
            result.add(resultDto);
        }
        return result;
    }

    private Map<String, List<GenreContentDto>> recommendContentsByUserGenre(User user, int count) {
        List<String> preferGenresByUser = preferMetaInfoRepository.findPreferGenresByUser(user.getId(), count);
        Map<String, List<GenreContentDto>> result = new HashMap<>();
        for (String genre : preferGenresByUser) {
            List<TopContentsByGenreDto> topContentsByGenre = contentRepository.findTopContentsByGenre(genre, 5);
            result.put(genre, topContentsByGenre.stream().map(tc -> new GenreContentDto(tc.contentId(), tc.thumbnailUrl())).toList());
        }
        return result;
    }
    private List<String> getGenres(Content content) {
        List<Map<String, Object>> contentGenresByContentIds = contentRepository.findContentGenresByContentIds(List.of(content.getId()));
        List<String> genres = new ArrayList<>();
        for (Map<String, Object> genreInfo : contentGenresByContentIds) {
            String genreName = (String) genreInfo.get("genreName");
            genres.add(genreName);
        }
        return genres;
    }

    private List<FastApiRecommendResponseDto> fastApiVectorRecommend(String vector, int count) {
        return fastApiClient.getContentsByVector(vector, count);
    }
}
