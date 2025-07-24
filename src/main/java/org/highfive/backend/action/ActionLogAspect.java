package org.highfive.backend.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.action.log.ActionLogStatus;
import org.highfive.backend.action.log.ActionLogStrategyFactory;
import org.highfive.backend.action.log.MetaInfoLog;
import org.highfive.backend.action.log.strategy.ActionLogStrategy;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.user.code.UserErrorCode;
import org.highfive.backend.user.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

import static org.highfive.backend.action.log.ActionLogMapper.extractMetaInfoLog;

@Slf4j
@Aspect
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ActionLogAspect {

    private final ActionLogService actionLogService;
    private final ActionLogStrategyFactory strategyFactory;
    private final MetaInfoContentsRepository metaInfoContentsRepository;

    @Around("@annotation(org.highfive.backend.action.ActionLogStamp)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        final ActionLog actionLog = createActionLog(joinPoint);
        final Long contentId = actionLog.getContentId();
        final List<MetaInfoContents> metaInfoContents = metaInfoContentsRepository.findByContentIdWithMetaInfo(contentId);
        final MetaInfoLog metaInfoLog = extractMetaInfoLog(metaInfoContents);
        actionLog.updateMetaInfo(metaInfoLog);
        actionLog.updateStatus(ActionLogStatus.PROCESSING);

        try {
            Object result = joinPoint.proceed();
            ActionLog savedActionLog = actionLogService.saveLog(actionLog);
            actionLogService.publishUpdateWeightMessage(savedActionLog);
            return result;
        } catch (Exception e) {
            log.error("행동 로그 저장 중 에러가 발생했습니다.");
            throw e;
        }
    }

    private ActionLog createActionLog(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ActionLogStamp actionLogStamp = method.getAnnotation(ActionLogStamp.class);
        Action action = actionLogStamp.value();

        long userId = extractUserId();
        long timestamp = System.currentTimeMillis();

        ActionLogStrategy strategy = strategyFactory.getStrategy(action);
        return strategy.createLog(joinPoint, userId, timestamp);
    }

    private long extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(UserErrorCode.UNAUTHORIZED_USER);
        }

        Object principal = authentication.getPrincipal();
        return ((User) principal).getId();
    }
}
