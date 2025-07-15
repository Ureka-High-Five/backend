package org.highfive.backend.content.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.dto.mapper.ShortsLikeTimeLogMapper;
import org.highfive.backend.content.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;
import org.highfive.backend.content.exception.ShortsErrorCode;
import org.highfive.backend.content.repository.jpa.ShortsLikeTimeLogRepository;

import static org.highfive.backend.content.dto.mapper.ShortsLikeTimeLogMapper.*;
import static org.highfive.backend.content.exception.ShortsErrorCode.SHORTS_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.dto.mapper.ShortsCommentMapper;
import org.highfive.backend.content.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.content.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.content.entity.shorts.ShortsComment;
import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;
import org.highfive.backend.content.exception.ShortsErrorCode;
import org.highfive.backend.content.repository.jpa.ShortsCommentRepository;
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
    private final ShortsCommentRepository shortsCommentRepository;
    private final ShortsQueryRepository shortsQueryRepository;

    @Transactional
    public Response<Void> createShortsComment(CreateShortsCommentRequestDto requestDto, User user) {
        //Todo: 쇼츠 댓글에 대한 금칙어 처리 필요

        Shorts existedShorts = shortsRepository.findById(requestDto.shortsId())
                .orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        ShortsComment shortsComment = ShortsCommentMapper.toShortsComment(requestDto, user, existedShorts);
        shortsCommentRepository.save(shortsComment);

        return Response.ok(null);
    }

    public Response<RecommendShortsResponseDto> recommendShorts(final Long cursor, final Integer size) {
        return Response.ok(new RecommendShortsResponseDto(
                shortsQueryRepository.findByCursor(cursor == null ? null : cursor.toString(), size),
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
        shortsLikeTimeLogRepository.save(toShorts(user, shorts, time));
        shortsRepository.increaseLike(shortsId);

        return Response.ok(null);
    }
}
