package org.highfive.backend.content.dto.mapper;

import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;
import org.highfive.backend.user.entity.User;

public class ShortsLikeTimeLogMapper {

    public static ShortsLikeTimeLog toShorts(final User user, final Shorts shorts, final long time) {
        return ShortsLikeTimeLog.builder()
                .user(user)
                .shorts(shorts)
                .time(time)
                .build();
    }
}
