package org.highfive.backend.action.log.strategy;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.dto.request.ShortsLikeCreateRequestDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.exception.ShortsErrorCode;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeActionLogStrategy implements ActionLogStrategy {

    private final ShortsRepository shortsRepository;

    @Override
    public ActionLog createLog(ProceedingJoinPoint joinPoint, long userId, long timestamp) {
        long shortsId = extractShortsLikeCreateRequestDto(joinPoint);
        long contentId = getContentIdByShortsId(shortsId);
        return ActionLog.builder()
                .userId(userId)
                .contentId(contentId)
                .action(Action.LIKE)
                .value(1)
                .timestamp(timestamp)
                .build();
    }

    private long extractShortsLikeCreateRequestDto(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof ShortsLikeCreateRequestDto dto) {
                return dto.shortsId();
            }
        }
        throw new BusinessException(GlobalErrorCode.BAD_REQUEST);
    }


    private long getContentIdByShortsId(long shortsId) {
        Shorts shorts = shortsRepository.findById(shortsId)
                .orElseThrow(() -> new BusinessException(ShortsErrorCode.SHORTS_NOT_FOUND));
        return shorts.getContent().getId();
    }
}
