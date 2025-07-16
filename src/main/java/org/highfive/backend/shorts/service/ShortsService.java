package org.highfive.backend.shorts.service;


import static org.highfive.backend.shorts.dto.mapper.ShortsLikeTimeLogMapper.toShorts;
import static org.highfive.backend.shorts.exception.ShortsErrorCode.SHORTS_ALREADY_LIKED;
import static org.highfive.backend.shorts.exception.ShortsErrorCode.SHORTS_LIKED_NOT_FOUND;
import static org.highfive.backend.shorts.exception.ShortsErrorCode.SHORTS_NOT_FOUND;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.dto.mapper.ShortsCommentMapper;
import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsDislikeRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.shorts.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsAndLikedItemDto;
import org.highfive.backend.shorts.dto.response.ShortsItemDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.highfive.backend.shorts.repository.jpa.ShortsCommentRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.shorts.repository.querydsl.ShortsQueryRepository;
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
    public Response<Void> dislike(final User user, final ShortsDislikeRequestDto dto) {
        final Long shortsId = dto.shortsId();

        shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        final ShortsLikeTimeLog shortsLikeTimeLog = shortsLikeTimeLogRepository.findByUserIdAndShortsId(user.getId(),shortsId)
                .orElseThrow(() -> new BusinessException(SHORTS_LIKED_NOT_FOUND));

        shortsLikeTimeLogRepository.deleteById(shortsLikeTimeLog.getId());
        shortsRepository.decreaseLike(shortsId);

        return Response.ok(null);
    }

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
    public Response<Void> like(final User user, final ShortsLikeRequestDto dto) {
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

    private List<ShortsAndLikedItemDto> getRecommendResult(User user, CursorPageResponse<ShortsItemDto> recommend) {
        return recommend.items().stream().map(item -> {
            boolean liked = shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), item.shortsId());
            return new ShortsAndLikedItemDto(item.contentId(), item.contentTitle(), item.shortsId(), item.shortsUrl(), liked);
        }).toList();
    }
}
