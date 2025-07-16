package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
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
