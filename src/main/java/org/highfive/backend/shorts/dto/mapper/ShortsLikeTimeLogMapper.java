package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.dto.response.ShortsLikeTimeResponseDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.highfive.backend.user.entity.User;

import java.util.List;

public class ShortsLikeTimeLogMapper {

    public static ShortsLikeTimeLog toShorts(final User user, final Shorts shorts, final long time) {
        return ShortsLikeTimeLog.builder()
                .user(user)
                .shorts(shorts)
                .time(time)
                .build();
    }

    public static List<ShortsLikeTimeResponseDto.ShortsLikeTimeLineDto> toShortsLikeTimeLineDto(final List<Object[]> results) {
        return results.stream()
                .map(row -> new ShortsLikeTimeResponseDto.ShortsLikeTimeLineDto(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue()
                ))
                .toList();
    }
}
