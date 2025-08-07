package org.highfive.backend.action.log.strategy;

import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.dto.request.UpdateUserWeightByClickRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ClickActionLogStrategy implements ActionLogStrategy {

    @Override
    public ActionLog createLog(ProceedingJoinPoint joinPoint, long userId, long timestamp) {
        long contentId = extractContentId(joinPoint);
        return ActionLog.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .contentId(contentId)
                .action(Action.CLICK)
                .value(1)
                .timestamp(timestamp)
                .build();
    }

    private long extractContentId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof UpdateUserWeightByClickRequestDto dto) {
                return dto.contentId();
            }
        }

        throw new BusinessException(ContentErrorCode.ID_CASTING_ERROR);
    }
}
