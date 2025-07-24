package org.highfive.backend.action.log.strategy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

        for (int i = 0; i < paramNames.length; i++) {
            if ("contentId".equals(paramNames[i]) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }
        throw new BusinessException(ContentErrorCode.ID_CASTING_ERROR);
    }
}
