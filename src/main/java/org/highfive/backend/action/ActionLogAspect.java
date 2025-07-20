package org.highfive.backend.action;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeCreateRequestDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.exception.ShortsErrorCode;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.user.code.UserErrorCode;
import org.highfive.backend.user.dto.request.CreateContentWatchLogRequestDto;
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
    private final ShortsRepository shortsRepository;
    private final ContentRepository contentRepository;

    private record ContentWatchLogInfo(long id, int watchTime, String type) {}
    private record ContentReviewLogInfo(long contentId, int rating) {}

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
        long timestamp = System.currentTimeMillis();

        if (action == Action.CLICK) {
            long contentId = extractContentId(joinPoint);
            return ActionLog.builder()
                    .userId(userId)
                    .contentId(contentId)
                    .action(action)
                    .value(1)
                    .timestamp(timestamp)
                    .build();
        }

        if (action == Action.WATCH) { // todo : 시청 시간 뽑고 시청 비율 계산
            ContentWatchLogInfo info = extractContentWatchLogInfo(joinPoint);
            long contentId;
            double watchRate;
            int watchTime = info.watchTime;
            if (info.type.equals("SHORTS")) {
                long shortsId = info.id;
                Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new BusinessException(ShortsErrorCode.SHORTS_NOT_FOUND));
                contentId = shorts.getContent().getId();
                watchRate = calcWatchRate(shorts.getRunningTime(), watchTime);
            } else if (info.type.equals("VIDEO")) {
                contentId = info.id;
                Content content = contentRepository.findById(contentId).orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
                watchRate = calcWatchRate(content.getRunningTime(), watchTime);
            } else {
                throw new BusinessException(ContentErrorCode.VIDEO_TYPE_NOT_FOUND);
            }

            return ActionLog.builder()
                    .userId(userId)
                    .contentId(contentId)
                    .action(action)
                    .value(watchRate)
                    .timestamp(timestamp)
                    .build();
        }

        if (action == Action.RATING) { // todo : 평점 추출

        }

        if (action == Action.LIKE) {

        }
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
        Object[] args = joinPoint.getArgs(); // 파라미터 값

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
