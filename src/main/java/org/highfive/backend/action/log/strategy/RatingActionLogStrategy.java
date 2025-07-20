package org.highfive.backend.action.log.strategy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
import org.springframework.stereotype.Component;

@Component
public class RatingActionLogStrategy implements ActionLogStrategy {

    private record ContentReviewLogInfo(long contentId, int rating) {}

    @Override
    public ActionLog createLog(ProceedingJoinPoint joinPoint, long userId, long timestamp) {
        ContentReviewLogInfo info = extractContentReviewLogInfo(joinPoint);
        return ActionLog.builder()
                .userId(userId)
                .contentId(info.contentId)
                .action(Action.RATING)
                .value(info.rating)
                .timestamp(timestamp)
                .build();
    }

    private ContentReviewLogInfo extractContentReviewLogInfo(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof CreateReviewRequestDto dto) {
                return new ContentReviewLogInfo(dto.contentId(), dto.rating());
            }
        }
        throw new BusinessException(GlobalErrorCode.BAD_REQUEST);
    }
}
