package org.highfive.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.ContentDetailDto;
import org.highfive.backend.content.dto.MetaInfoDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.highfive.backend.content.dto.mapper.ContentMapper.toSearchContentResponseDto;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final String LIKE = "%";

    private final ContentRepository contentRepository;
    private final ContentQueryRepositoryImpl contentQueryRepositoryImpl;

    public Response<ContentDetailResponseDto> getContentDetailById(final long contentId) {

        final ContentDetailDto contentDetailDto = contentQueryRepositoryImpl.findContentDetailById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));

        final MetaInfoDto metaInfoDto = contentQueryRepositoryImpl.findMetaInfoById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_META_INFO_NOT_FOUND));

        final ContentDetailResponseDto response = ContentMapper.toContentDetailResponseDto(
                contentDetailDto, metaInfoDto.director(), metaInfoDto.actors(), metaInfoDto.genres()
        );

        return Response.ok(response);
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

    public Response<ContentVideoResponseDto> getContentVideo(final Long contentId) {
        final Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));

        ContentVideoResponseDto response = new ContentVideoResponseDto(content.getVideoUrl(),
                VideoType.VIDEO.toString());

        return Response.ok(response);
    }
}
