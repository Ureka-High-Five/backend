package org.highfive.backend.content.dto.response;

import java.util.List;

public record ContentDetailResponseDto(
        String contentTitle,
        List<String> contentGenres,
        int contentRunningTime,
        int contentGrade,
        int userRating,
        String userReview,
        String posterUrl,
        List<String> actors,
        String director,
        String openDate
) {
}
