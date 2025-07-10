package org.highfive.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.TopContentByGenreDto;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.highfive.backend.content.dto.mapper.ContentMapper.toSearchContentResponseDto;
import static org.highfive.backend.global.code.SuccessCode.OK;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int INIT_CONTENT_CNT = 6;
    private final String LIKE = "%";

    private final ContentRepository contentRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;

    public List<OnboardingInitContentsResponseDto> getDistinctGenreTopContents() {
        final List<TopContentByGenreDto> topContents = contentRepository.findTopContentPerGenre(INIT_CONTENT_CNT);

        return topContents.stream()
                .map(dto -> new OnboardingInitContentsResponseDto(dto.getId(), dto.getThumbnailUrl(), dto.getTitle()))
                .toList();
    }

    public Response<ContentDetailResponseDto> getContentDetail(final Long contentId) {

        final Content content = getContentOrThrow(contentId);
        final Map<MetaType, List<String>> metaMap = getMetaInfoMap(contentId);

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

    private Content getContentOrThrow(final Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    private Map<MetaType, List<String>> getMetaInfoMap(final Long contentId) {
        final List<MetaInfoContents> infoContentsList = metaInfoContentsRepository.findByContentId(contentId);

        return infoContentsList.stream()
                .map(MetaInfoContents::getMetaInfo)
                .collect(Collectors.groupingBy(
                        MetaInfo::getType,
                        Collectors.mapping(MetaInfo::getName, Collectors.toList())
                ));
    }

    private String extractDirector(final Map<MetaType, List<String>> metaMap) {
        return metaMap.getOrDefault(MetaType.DIRECTOR, List.of())
                .stream()
                .findFirst()
                .orElse(null);
    }

}
