package org.highfive.backend.shorts.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.shorts.dto.ShortsDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShortsRedisRepository {

    private final String SHORTS_KEY_PREFIX = "shorts:";
    private final int TTL_MINUTE = 30;

    private final RedisTemplate<String, ShortsDto> redisTemplate;

    public void saveAll(final Long userId, final List<ShortsDto> shorts) {
        String key = SHORTS_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        redisTemplate.opsForList().rightPushAll(key, shorts);
        redisTemplate.expire(key, Duration.ofMinutes(TTL_MINUTE));
    }

    public List<ShortsDto> findByCursor(final Long userId, final Long cursorId, final int size) {
        String key = SHORTS_KEY_PREFIX + userId;
        List<ShortsDto> all = redisTemplate.opsForList().range(key, 0, -1);
        if (all == null || all.isEmpty()) return Collections.emptyList();

        int startIndex = 0;
        if (cursorId != null) {
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).id().equals(cursorId)) {
                    startIndex = i;
                    break;
                }
            }
        }

        int endIndex = Math.min(startIndex + size, all.size());
        return all.subList(startIndex, endIndex);
    }

    public List<Long> findShortsIdsByUserId(final Long userId) {
        String key = SHORTS_KEY_PREFIX + userId;
        List<ShortsDto> all = redisTemplate.opsForList().range(key, 0, -1);
        if (all == null || all.isEmpty()) return Collections.emptyList();

        return all.stream()
                .map(ShortsDto::contentId)
                .distinct()
                .toList();
    }
}
