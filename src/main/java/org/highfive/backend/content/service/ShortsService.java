package org.highfive.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.repository.querydsl.ShortsQueryRepository;
import org.highfive.backend.global.dto.Response;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortsService {

    private final ShortsQueryRepository shortsQueryRepository;

    public Response<RecommendShortsResponseDto> recommendShorts(final Long cursor, final Integer size) {
        return Response.ok(new RecommendShortsResponseDto(
                shortsQueryRepository.findByCursor(cursor == null ? null : cursor.toString(), size),
                VideoType.SHORTS.name())
        );
    }
}
