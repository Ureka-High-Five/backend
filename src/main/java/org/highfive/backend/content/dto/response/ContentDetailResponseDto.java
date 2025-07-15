package org.highfive.backend.content.dto.response;

import java.util.List;

public record ContentDetailResponseDto(
        String contentTitle,
        List<String> contentGenres,
        int contentRunningTime,
        int contentGrade,
        String posterUrl,
        List<String> actors,
        String director,
        int openYear,
        String contentDescription
) {
}
