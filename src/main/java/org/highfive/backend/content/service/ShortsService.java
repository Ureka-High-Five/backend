package org.highfive.backend.content.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;
import org.highfive.backend.content.exception.ShortsErrorCode;
import org.highfive.backend.content.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.content.repository.jpa.ShortsRepository;
import org.highfive.backend.content.repository.querydsl.ShortsQueryRepository;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortsService {

    private final ShortsLikeTimeLogRepository shortsLikeTimeLogRepository;
    private final ShortsRepository shortsRepository;
    private final ShortsQueryRepository shortsQueryRepository;

    public Response<RecommendShortsResponseDto> recommendShorts(final Long cursor, final Integer size) {
        return Response.ok(new RecommendShortsResponseDto(
                shortsQueryRepository.findByCursor(cursor.toString(), size),
                VideoType.SHORTS.name())
        );
    }

    @Transactional
    public Response<Void> like(final User user, final ShortsLikeRequestDto dto) {
        final Long shortsId = dto.shortsId();
        final Long time = dto.time();
        final Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(ShortsErrorCode.SHORTS_NOT_FOUND));

        if(shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), shortsId)) {
            throw new BusinessException(ShortsErrorCode.SHORTS_ALREADY_LIKED);
        }

        final ShortsLikeTimeLog log = ShortsLikeTimeLog.builder()
                .user(user)
                .shorts(shorts)
                .time(time)
                .build();

        shortsLikeTimeLogRepository.save(log);
        shortsRepository.increaseLike(shortsId);

        return Response.ok(null);
    }
}
