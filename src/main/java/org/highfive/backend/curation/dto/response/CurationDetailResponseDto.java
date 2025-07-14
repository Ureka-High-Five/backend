package org.highfive.backend.curation.dto.response;

import org.highfive.backend.content.entity.Content;

import java.util.List;

public record CurationDetailResponseDto(
        String title,
        List<ContentDto> contents,
        String thumbnailUrl,
        String profileUrl,
        String editorName,
        long editorId
) {

    public record ContentDto(
            long id,
            String title,
            String thumbnailUrl
    ) {}
}
