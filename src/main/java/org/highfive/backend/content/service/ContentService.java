package org.highfive.backend.content.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.TopContentByGenreDto;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.entity.review.Review;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.ReviewRepository;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int INIT_CONTENT_CNT = 6;

    private final ContentRepository contentRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;
    private final ReviewRepository reviewRepository;

    public List<OnboardingInitContentsResponseDto> getDistinctGenreTopContents() {
        List<TopContentByGenreDto> topContents = contentRepository.findTopContentPerGenre(INIT_CONTENT_CNT);

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

        Review review = reviewRepository.findByUserAndContent(user, existedContent).orElse(null);
        Integer rating = (review != null) ? review.getRating() : null;
        String reviewText = (review != null) ? review.getReviewText() : null;

        ContentDetailResponseDto response = ContentMapper.toContentDetailResponseDto(existedContent, director, actors,
                genres, rating, reviewText);

        return new Response<>(SuccessCode.OK.getCode(), response, null);
    }
}
