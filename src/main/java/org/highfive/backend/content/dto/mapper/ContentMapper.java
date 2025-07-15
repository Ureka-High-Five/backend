package org.highfive.backend.content.dto.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.ContentType;
import org.highfive.backend.global.dto.CursorPageResponse;

import java.util.List;

public class ContentMapper {

    public static ContentDetailResponseDto toContentDetailResponseDto(Content content, String director,
                                                                      List<String> actors, List<String> genres) {
        return new ContentDetailResponseDto(content.getTitle(), genres, content.getRunningTime(), content.getGrade(),
                content.getPostUrl(), actors, director, content.getOpenDate().getYear(),content.getDescription());
    }

    public static SearchContentResponseDto toSearchContentResponseDto(final Content content) {
        return new SearchContentResponseDto(content.getId(), content.getPostUrl(), content.getTitle(), content.getOpenDate().getYear());
    }

    public static CursorPageResponse<SearchContentResponseDto> toSearchContentResponseDto(final List<Content> contents, final boolean hasNext, final String nextCursor) {
        final List<SearchContentResponseDto> results = contents.stream()
                .map(ContentMapper::toSearchContentResponseDto)
                .toList();
        return new CursorPageResponse<>(results, hasNext, nextCursor);

    }

    public static Content fromAdminAddContentRequestDto(AdminAddContentRequestDto request) {
        return Content.builder()
                .title(request.title())
                .description(request.description())
                .videoUrl(request.videoUrl())
                .postUrl(request.postUrl())
                .openDate(LocalDateTime.from(LocalDate.parse(request.openDate()).atStartOfDay()))
                .runningTime(request.runningTime())
                .totalRound(request.totalRound())
                .contentType(ContentType.valueOf(request.type()))
                .grade(15)
                .popularity(100)
                .build();
    }
}
