package org.highfive.backend.action;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.code.UserErrorCode;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActionLogAspect {

    private final ActionLogService actionLogService;

    @Around("@annotation(org.highfive.backend.action.ActionLogStamp)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ActionLog actionLog = createActionLog(joinPoint);

        try {
            Object result = joinPoint.proceed();
            actionLogService.saveLog(actionLog);
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
        long contentId = extractContentId(joinPoint);
        long timestamp = System.currentTimeMillis();
        return ActionLog.builder()
                .userId(userId)
                .contentId(contentId)
                .action(action)
                .timestamp(timestamp)
                .build();
    }

    private long extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(UserErrorCode.UNAUTHORIZED_USER);
        }

        Object principal = authentication.getPrincipal();

        return ((User) principal).getId();
    }

    private long extractContentId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames(); // 파라미터 이름
        Object[] args = joinPoint.getArgs();                 // 파라미터 값

        for (int i = 0; i < paramNames.length; i++) {
            if ("contentId".equals(paramNames[i])) {
                if (args[i] instanceof Long) {
                    return (Long) args[i];
                }
                throw new BusinessException(ContentErrorCode.ID_CASTING_ERROR);
            }
        }
        throw new BusinessException(GlobalErrorCode.BAD_REQUEST);
    }
}
