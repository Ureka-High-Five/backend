package org.highfive.backend.content.dto.mapper;

import org.highfive.backend.content.dto.ContentDetailDto;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.ContentType;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;

public class ContentMapper {

    @Value("${cloud.aws.s3.bucket}")
    private static String bucket;

    @Value("${cloud.aws.region.static}")
    private static String awsRegion;

    public static ContentDetailResponseDto toContentDetailResponseDto(ContentDetailDto dto, String director,
                                                                      List<String> actors, List<String> genres) {
        return new ContentDetailResponseDto(dto.title(), genres, dto.runningTime(), dto.grade(),
                dto.postUrl(), actors, director, dto.openDate().getYear(),dto.description(),
                dto.shortsId(), dto.videoUrl());
    }

    public static SearchContentResponseDto toSearchContentResponseDto(final Content content) {
        return new SearchContentResponseDto(content.getId(), content.getThumbnailUrl(), content.getTitle(),
                content.getOpenDate().getYear());
    }

    public static CursorPageResponse<SearchContentResponseDto> toSearchContentResponseDto(final List<Content> contents,
                                                                                          final boolean hasNext,
                                                                                          final String nextCursor) {
        final List<SearchContentResponseDto> results = contents.stream()
                .map(ContentMapper::toSearchContentResponseDto)
                .toList();
        return new CursorPageResponse<>(results, hasNext, nextCursor);

    }

    public static Content fromAdminAddContentRequestDto(AdminAddContentRequestDto request, final String videoUrl) {
        return Content.builder()
                .title(request.title())
                .description(request.description())
                .videoUrl(videoUrl)
                .postUrl(request.postUrl())
                .openDate(LocalDate.parse(request.openDate()).atStartOfDay())
                .runningTime(request.runningTime())
                .totalRound(request.totalRound())
                .contentType(ContentType.valueOf(request.type()))
                .grade(15)
                .popularity(100)
                .thumbnailUrl(request.postUrl())
                .trailerTime(request.trailerTime())
                .build();
    }
}
