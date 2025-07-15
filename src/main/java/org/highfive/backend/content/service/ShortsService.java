package org.highfive.backend.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.dto.mapper.ShortsMapper;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.dto.response.ShortsItemDto;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.content.repository.querydsl.ShortsQueryRepository;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortsService {

    private final ShortsQueryRepository shortsQueryRepository;

    public Response<RecommendShortsResponseDto> mockRecommendShorts(Long cursor, Integer size) {
        return Response.ok(new RecommendShortsResponseDto(
                shortsQueryRepository.findByCursor(cursor.toString(), size),
                VideoType.SHORTS.name())
        );
    }
}
