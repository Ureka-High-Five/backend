package org.highfive.backend.curation.dto.request;

import org.highfive.backend.curation.dto.request.customAnnotation.NoDuplicate;

import java.util.List;

public record CurationUpdateRequestDto(

        @NoDuplicate
        List<Long> contents,
        String title,
        String thumbnailUrl
) {
}
