package org.highfive.backend.shorts.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.dto.ShortsDto;
import org.highfive.backend.shorts.dto.mapper.ShortsCommentMapper;
import org.highfive.backend.shorts.dto.mapper.ShortsMapper;
import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsDislikeRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeCreateRequestDto;
import org.highfive.backend.shorts.dto.response.*;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.highfive.backend.shorts.repository.jpa.ShortsCommentRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsLikeTimeLogRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRedisRepository;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.shorts.repository.querydsl.ShortsCommentQueryRepository;
import org.highfive.backend.slang.SlangValidator;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.highfive.backend.user.repository.redis.UserRedisRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.highfive.backend.global.util.VectorUtil.convertUserVector;
import static org.highfive.backend.shorts.dto.mapper.ShortsLikeTimeLogMapper.toShorts;
import static org.highfive.backend.shorts.dto.mapper.ShortsLikeTimeLogMapper.toShortsLikeTimeLineDto;
import static org.highfive.backend.shorts.dto.mapper.ShortsMapper.toShortsLikedUserResponseDtos;
import static org.highfive.backend.shorts.exception.ShortsErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortsService {

    private final int RECOMMEND_SHORTS_COUNT = 20;
    private final int RECOMMEND_RANDOM_COUNT = 10;

    private final ShortsLikeTimeLogRepository shortsLikeTimeLogRepository;
    private final ShortsRepository shortsRepository;
    private final ShortsRedisRepository shortsRedisRepository;
    private final ShortsCommentRepository shortsCommentRepository;
    private final ShortsCommentQueryRepository shortsCommentQueryRepository;
    private final UserRedisRepository userRedisRepository;
    private final UserRepository userRepository;

    @Transactional
    public Response<CreateShortsCommentResponseDto> createShortsComment(CreateShortsCommentRequestDto requestDto, User user) {
        SlangValidator.validate(requestDto.comment());

        Shorts existedShorts = shortsRepository.findById(requestDto.shortsId())
                .orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        ShortsComment shortsComment = ShortsCommentMapper.toShortsComment(requestDto, user, existedShorts);
        ShortsComment saved = shortsCommentRepository.save(shortsComment);
        CreateShortsCommentResponseDto response = ShortsCommentMapper.toCreateShortsCommentResponseDto(saved);

        return Response.ok(response);
    }

    @Transactional
    public Response<CursorPageResponse<ShortsResponseDto>> recommendShorts(final Long cursor, final Integer size, final User user) {
        final String userVector = convertUserVector(userRedisRepository.getUserVector(user.getId()));
        userRepository.upsertUserVector(user.getId(), userVector);

        if (cursor == null || cursor == 0L) {
            generateShortsCache(user.getId());
        }

        final List<ShortsDto> pagedShorts = shortsRedisRepository.findByCursor(user.getId(), cursor, size + 1);
        final boolean hasNext = pagedShorts.size() > size;
        final Long nextCursor = hasNext ? pagedShorts.get(size).id() : null;
        final List<ShortsDto> sliced = hasNext ? pagedShorts.subList(0, size) : pagedShorts;
        final List<ShortsResponseDto> result = getRecommendResult(user, sliced);
        return Response.ok(new CursorPageResponse<>(result, hasNext, nextCursor != null ? nextCursor.toString() : null));
    }

    @Transactional
    public Response<Void> like(final User user, final ShortsLikeCreateRequestDto dto) {
        final Long shortsId = dto.shortsId();
        final long time = dto.time();
        final Shorts shorts = shortsRepository.findById(shortsId)
                .orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        if (shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), shortsId)) {
            throw new BusinessException(SHORTS_ALREADY_LIKED);
        }
        shortsLikeTimeLogRepository.save(toShorts(user, shorts, time));
        shortsRepository.increaseLike(shortsId);

        return Response.ok(null);
    }

    public Response<List<ShortsCommentsByTimeResponseDto>> getCommentsByTime(long shortsId, long time, int duration) {
        List<ShortsCommentsByTimeResponseDto> response = new ArrayList<>();
        for (long targetTime = time; targetTime < time + duration; targetTime++) {
            Optional<ShortsComment> optionalComment = shortsCommentRepository.findFirstByShortsIdAndTimeOrderByCreatedAtDesc(shortsId, targetTime);
            if (optionalComment.isPresent()) {
                ShortsComment shortsComment = optionalComment.get();
                response.add(ShortsCommentMapper.toShortsCommentsByTimeResponseDto(shortsComment));
            }
        }

        return Response.ok(response);
    }

    @Transactional
    public Response<Void> dislike(final User user, final ShortsDislikeRequestDto dto) {
        final Long shortsId = dto.shortsId();

        shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));

        final ShortsLikeTimeLog shortsLikeTimeLog = shortsLikeTimeLogRepository.findByUserIdAndShortsId(user.getId(),
                        shortsId)
                .orElseThrow(() -> new BusinessException(SHORTS_LIKED_NOT_FOUND));

        shortsLikeTimeLogRepository.deleteById(shortsLikeTimeLog.getId());
        shortsRepository.decreaseLike(shortsId);

        return Response.ok(null);
    }

    public Response<GetShortsCommentResponseDto> getOneShortsComment(final Long shortsId, final Long time) {

        final ShortsComment existedShortsComment = shortsCommentRepository.findFirstByShortsIdAndTimeOrderByCreatedAtDesc(
                shortsId, time).orElse(null);

        if (Objects.isNull(existedShortsComment)) {
            return Response.ok(null);
        }

        GetShortsCommentResponseDto responseDto = ShortsCommentMapper.toGetShortsCommentResponseDto(
                existedShortsComment);

        return Response.ok(responseDto);
    }

    public Response<ShortsLikeTimeResponseDto> getShortsLike(final long shortsId, final int duration, final User user) {
        shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));
        List<Object[]> results = shortsLikeTimeLogRepository.findAllShortsLikeWithTime(shortsId, duration);
        List<ShortsLikeTimeResponseDto.ShortsLikeTimeLineDto> data = toShortsLikeTimeLineDto(results);
        boolean liked = shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), shortsId);

        return Response.ok(new ShortsLikeTimeResponseDto(data, liked));
    }

    public Response<CursorPageResponse<ShortsLikedUserItemDto>> likedShorts(final User user, final String cursor,
                                                                            final int size) {
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
        return Response.ok(new CursorPageResponse<>(toShortsLikedUserResponseDtos(results), hasNext, nextCursor));
    }

    public Response<CursorPageResponse<ShortsCommentsByIdResponseDto>> commentsByIdAndCursor(Long shortsId, Long cursor,
                                                                                             Integer size) {
        return Response.ok(shortsCommentQueryRepository.findByIdAndCursor(shortsId, cursor, size));
    }

    public Response<ShortsResponseDto> getShortsById(long shortsId, User user) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));
        boolean liked = shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), shorts.getId());
        return Response.ok(ShortsMapper.toShortsResponseDto(shorts, liked));
    }

    private void generateShortsCache(final Long userId) {
        final List<ShortsDto> recommended = shortsRepository.findRecommendedShortsByUser(userId, RECOMMEND_SHORTS_COUNT);

        final List<Long> contentIds = recommended.stream()
                .map(ShortsDto::contentId)
                .distinct()
                .toList();

        final List<ShortsDto> random = shortsRepository.findRandomShortsExcludingContentIds(contentIds, RECOMMEND_RANDOM_COUNT);
        final List<ShortsDto> result = new ArrayList<>(recommended);
        result.addAll(random);
        Collections.shuffle(result);

        shortsRedisRepository.saveAll(userId, result);
    }

    private List<ShortsResponseDto> getRecommendResult(final User user, final List<ShortsDto> recommend) {
        final List<Long> shortsIds = recommend.stream()
                .map(ShortsDto::id)
                .toList();

        final List<Long> likedIds = shortsLikeTimeLogRepository.findLikedShortsIds(user.getId(), shortsIds);

        return recommend.stream()
                .map(item -> ShortsMapper.toShortsResponseDto(item, likedIds.contains(item.id())))
                .toList();
    }

    public Response<ShortsResponseDto> getShortsByContent(final Long contentId, User user) {
        Shorts randomShorts = shortsRepository.findRandomByContentId(contentId)
                .orElseThrow(() -> new BusinessException(SHORTS_NOT_FOUND));
        boolean liked = shortsLikeTimeLogRepository.existsByUserIdAndShortsId(user.getId(), randomShorts.getId());
        ShortsResponseDto response = ShortsMapper.toShortsResponseDto(randomShorts, liked);

        return Response.ok(response);
    }
}
