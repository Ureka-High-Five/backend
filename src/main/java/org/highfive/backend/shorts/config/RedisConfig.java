package org.highfive.backend.shorts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.highfive.backend.shorts.dto.ShortsDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ShortsDto> shortsDtoRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ShortsDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        serializer(template);
        return template;
    }

    private static void serializer(final RedisTemplate<String, ShortsDto> template) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Jackson2JsonRedisSerializer<ShortsDto> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, ShortsDto.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
    }
}
