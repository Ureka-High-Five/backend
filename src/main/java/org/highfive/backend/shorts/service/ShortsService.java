package org.highfive.backend.shorts.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.dto.mapper.ShortsCommentMapper;
import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsDislikeRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeCreateRequestDto;
import org.highfive.backend.shorts.dto.response.*;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.highfive.backend.shorts.repository.jpa.ShortsCommentRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.shorts.repository.querydsl.ShortsQueryRepository;
import org.highfive.backend.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.highfive.backend.shorts.dto.mapper.ShortsLikeTimeLogMapper.toShorts;
import static org.highfive.backend.shorts.dto.mapper.ShortsLikeTimeLogMapper.toShortsLikeTimeLineDto;
import static org.highfive.backend.shorts.dto.mapper.ShortsMapper.*;
import static org.highfive.backend.shorts.exception.ShortsErrorCode.*;

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

    public Response<RecommendShortsResponseDto> recommendShorts(final Long cursor, final Integer size, final User user) {
        CursorPageResponse<ShortsItemDto> recommend = shortsQueryRepository.findByCursor(cursor == null ? null : cursor.toString(), size);
        List<ShortsAndLikedItemDto> result = getRecommendResult(user, recommend);
        CursorPageResponse<ShortsAndLikedItemDto> response = new CursorPageResponse<>(result, recommend.hasNext(), recommend.nextCursor());
        return Response.ok(new RecommendShortsResponseDto(
                response,
                VideoType.SHORTS.name())
        );
    }

    @Transactional
    public Response<Void> like(final User user, final ShortsLikeCreateRequestDto dto) {
        final Long shortsId = dto.shortsId();
        final long time = dto.time();
        final Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        if (shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), shortsId)) {
            throw new BusinessException(SHORTS_ALREADY_LIKED);
        }
        shortsLikeTimeLogRepository.save(toShorts(user, shorts, time));
        shortsRepository.increaseLike(shortsId);

        return Response.ok(null);
    }

    @Transactional
    public Response<Void> dislike(final User user, final ShortsDislikeRequestDto dto) {
        final Long shortsId = dto.shortsId();

        shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        final ShortsLikeTimeLog shortsLikeTimeLog = shortsLikeTimeLogRepository.findByUserIdAndShortsId(user.getId(),shortsId)
                .orElseThrow(() -> new BusinessException(SHORTS_LIKED_NOT_FOUND));

        shortsLikeTimeLogRepository.deleteById(shortsLikeTimeLog.getId());
        shortsRepository.decreaseLike(shortsId);

        return Response.ok(null);
    }

    public Response<GetShortsCommentResponseDto> getOneShortsComment(final Long shortsId,final Long time) {

        final ShortsComment existedShortsComment = shortsCommentRepository.findFirstByShortsIdAndTimeOrderByCreatedAtDesc(shortsId,time).orElse(null);

        if(Objects.isNull(existedShortsComment)){
            return Response.ok(null);
        }

        GetShortsCommentResponseDto responseDto = ShortsCommentMapper.toGetShortsCommentResponseDto(existedShortsComment);

        return Response.ok(responseDto);
    }

    public Response<ShortsLikeTimeResponseDto> getShortsLike(final long shortsId, final int duration) {
        shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));
        List<Object[]> results = shortsLikeTimeLogRepository.findAllShortsLikeWithTime(shortsId, duration);
        List<ShortsLikeTimeResponseDto.ShortsLikeTimeLineDto> data = toShortsLikeTimeLineDto(results);

        return Response.ok(new ShortsLikeTimeResponseDto(data));
    }

    public Response<CursorPageResponse<ShortsLikedUserItemDto>> likedShorts(final User user, final String cursor, final int size) {
        Long cursorId = (cursor != null) ? Long.parseLong(cursor) : Long.MAX_VALUE;

        List<Shorts> results = shortsLikeTimeLogRepository.findLikedShorts(
                user.getId(),
                cursorId,
                PageRequest.of(0, size + 1)
        );

        final boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        final String nextCursor = hasNext ? results.get(results.size() - 1).getId().toString() : null;
        return Response.ok(new CursorPageResponse<>(toShortsLikedUserResponseDtos(results) , hasNext, nextCursor));
    }

    private List<ShortsAndLikedItemDto> getRecommendResult(final User user, final CursorPageResponse<ShortsItemDto> recommend) {
        return recommend.items().stream().map(item -> {
            boolean liked = shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), item.shortsId());
            return new ShortsAndLikedItemDto(item.contentId(), item.contentTitle(), item.shortsId(), item.shortsUrl(), liked);
        }).toList();
    }
}
