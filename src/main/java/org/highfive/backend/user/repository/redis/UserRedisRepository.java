package org.highfive.backend.user.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public String getUserVector(final Long userId) {
        return stringRedisTemplate.opsForValue().get(userId.toString());
    }

    public void setUserVector(final Long userId, final String vector) {
        stringRedisTemplate.opsForValue().set(userId.toString(), vector);
    }
}
