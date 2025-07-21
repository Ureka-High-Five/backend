package org.highfive.backend.curation.dto.request;

import java.util.List;
import org.highfive.backend.curation.dto.request.customAnnotation.NoDuplicate;

public record CurationUpdateRequestDto(

        @NoDuplicate
        List<Long> contents,
        String title,
        String thumbnailUrl
) {
}
