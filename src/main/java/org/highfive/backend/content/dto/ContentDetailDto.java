package org.highfive.backend.content.dto;

import java.time.LocalDateTime;

public record ContentDetailDto(
        String title,
        int runningTime,
        int grade,
        String postUrl,
        LocalDateTime openDate,
        String description,
        Long shortsId,
        String videoUrl
) {
}
