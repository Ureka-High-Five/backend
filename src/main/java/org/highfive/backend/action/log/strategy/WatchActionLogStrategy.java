package org.highfive.backend.action.log.strategy;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.exception.ShortsErrorCode;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.user.dto.request.CreateContentWatchLogRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WatchActionLogStrategy implements ActionLogStrategy {

    private final ContentRepository contentRepository;
    private final ShortsRepository shortsRepository;

    private record ContentWatchLogInfo(long id, int watchTime, String type) {
    }

    @Override
    public ActionLog createLog(ProceedingJoinPoint joinPoint, long userId, long timestamp) {
        ContentWatchLogInfo info = extractContentWatchLogInfo(joinPoint);
        long contentId;
        int watchTime = info.watchTime;
        double watchRate;

        if ("SHORTS".equals(info.type)) {
            Shorts shorts = shortsRepository.findById(info.id)
                    .orElseThrow(() -> new BusinessException(ShortsErrorCode.SHORTS_NOT_FOUND));
            contentId = shorts.getContent().getId();
            watchRate = calcWatchRate(shorts.getRunningTime(), watchTime);
        } else if ("VIDEO".equals(info.type)) {
            Content content = contentRepository.findById(info.id)
                    .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
            contentId = content.getId();
            watchRate = calcWatchRate(content.getRunningTime(), watchTime);
        } else {
            throw new BusinessException(ContentErrorCode.VIDEO_TYPE_NOT_FOUND);
        }

        return ActionLog.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .contentId(contentId)
                .action(Action.WATCH)
                .value(watchRate)
                .timestamp(timestamp)
                .build();
    }

    private ContentWatchLogInfo extractContentWatchLogInfo(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof CreateContentWatchLogRequestDto dto) {
                return new ContentWatchLogInfo(dto.id(), dto.watchTime(), dto.type());
            }
        }
        throw new BusinessException(GlobalErrorCode.BAD_REQUEST);
    }

    private double calcWatchRate(int runningTime, int watchTime) {
        double ratio = (double) watchTime / runningTime;
        double percent = ratio * 100.0;

        return Math.round(percent * 1000.0) / 1000.0;
    }
}

