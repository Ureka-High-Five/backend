package org.highfive.backend.user.service;

import static org.highfive.backend.content.exception.ContentErrorCode.CONTENT_NOT_FOUND;
import static org.highfive.backend.content.exception.ContentErrorCode.VIDEO_TYPE_NOT_FOUND;
import static org.highfive.backend.user.exception.UserLogErrorCode.INVALID_WATCH_TIME;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.VideoType;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.highfive.backend.user.dto.request.CreateContentWatchLogRequestDto;
import org.highfive.backend.user.repository.jpa.UserLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserLogService {

    private final ContentRepository contentRepository;
    private final ShortsRepository shortsRepository;
    private final UserLogRepository userLogRepository;

    @Transactional
    public Response<Void> createContentWatchLog(CreateContentWatchLogRequestDto request) {
        VideoType videoType = VideoType.valueOf(request.type());

        long runningTime;

        switch (videoType) {
            case VIDEO -> {
                Content foundContent = contentRepository.findById(request.id())
                        .orElseThrow(() -> new BusinessException(CONTENT_NOT_FOUND));
                runningTime = foundContent.getRunningTime();
            }
            case SHORTS -> {
                Shorts shorts = shortsRepository.findById(request.id())
                        .orElseThrow(() -> new BusinessException(CONTENT_NOT_FOUND));
                runningTime = shorts.getRunningTime();
            }
            default -> throw new BusinessException(VIDEO_TYPE_NOT_FOUND);
        }

        validateWatchTime(request.watchTime(), runningTime);

        return Response.ok(null);
    }

    private void validateWatchTime(long watchTime, long runningTime) {
        if (watchTime > runningTime) {
            throw new BusinessException(INVALID_WATCH_TIME);
        }
    }
}
