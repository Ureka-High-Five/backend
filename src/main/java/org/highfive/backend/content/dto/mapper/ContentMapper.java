package org.highfive.backend.content.dto.mapper;

import java.util.List;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.global.dto.CursorPageResponse;

public class ContentMapper {

    public static OnboardingInitContentsResponseDto toOnboardingInitContentsResponseDto(Content content) {
        return new OnboardingInitContentsResponseDto(content.getId(), content.getThumbnailUrl(), content.getTitle());
    }


    public static ContentDetailResponseDto toContentDetailResponseDto(Content content, String director,
                                                                      List<String> actors, List<String> genres) {
        return new ContentDetailResponseDto(content.getTitle(), genres, content.getRunningTime(), content.getGrade(),
                content.getPostUrl(), actors, director, content.getOpenDate().toString());
    }

    public static SearchContentResponseDto toSearchContentResponseDto(final Content content) {
        return new SearchContentResponseDto(content.getId(), content.getPostUrl(), content.getTitle(), content.getOpenDate().getYear());
    }

    public static CursorPageResponse<SearchContentResponseDto> toSearchContentResponseDto(final List<Content> contents, final Long nextCursor) {
        final List<SearchContentResponseDto> results = contents.stream()
                .map(ContentMapper::toSearchContentResponseDto)
                .toList();
        return new CursorPageResponse<>(results, String.valueOf(nextCursor));
    }
}
