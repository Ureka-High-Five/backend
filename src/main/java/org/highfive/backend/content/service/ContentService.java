package org.highfive.backend.content.service;

import static org.highfive.backend.content.dto.mapper.ContentMapper.toSearchContentResponseDto;
import static org.highfive.backend.global.code.SuccessCode.OK;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.ContentVideoResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.content.repository.querydsl.ContentQueryRepositoryImpl;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final String LIKE = "%";

    private final ContentRepository contentRepository;
    private final ContentQueryRepositoryImpl contentQueryRepositoryImpl;

    public Response<ContentDetailResponseDto> getContentDetail(final Long contentId) {

        final Content content = contentQueryRepositoryImpl.findWithMetaInfoById(contentId)
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

    public Response<CursorPageResponse<SearchContentResponseDto>> search(final String input, final String cursor,
                                                                         final int size) {
        final String keyword = LIKE + input.toLowerCase() + LIKE;
        Long parsedCursor = null;
        if (cursor != null && !cursor.isBlank()) {
            parsedCursor = Long.parseLong(cursor);
        }
        List<Content> contents = contentRepository.searchByInput(keyword, parsedCursor, Pageable.ofSize(size + 1));
        final boolean hasNext = contents.size() > size;
        contents = hasNext ? contents.subList(0, size) : contents;
        final String nextCursor = hasNext ? contents.get(contents.size() - 1).getId().toString() : null;

        return Response.ok(toSearchContentResponseDto(contents, hasNext, nextCursor));
    }

    private String extractDirector(final Map<MetaType, List<String>> metaMap) {
        return metaMap.getOrDefault(MetaType.DIRECTOR, List.of())
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Map<MetaType, List<String>> extractMetaInfoMap(Content content) {
        return content.getMetaInfoContents().stream()
                .map(MetaInfoContents::getMetaInfo)
                .collect(Collectors.groupingBy(
                        MetaInfo::getType,
                        Collectors.mapping(MetaInfo::getName, Collectors.toList())
                ));
    }

    public Response<ContentVideoResponseDto> getContentVideo(final Long contentId) {
        final Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));

        ContentVideoResponseDto response = new ContentVideoResponseDto(content.getVideoUrl(),
                VideoType.VIDEO.toString());

        return Response.ok(response);
    }
}
