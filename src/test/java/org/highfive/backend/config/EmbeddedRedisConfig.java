package org.highfive.backend.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    private int redisPort;

    private int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available port found", e);
        }
    }

    @PostConstruct
    public void startRedis() throws IOException {
        this.redisPort = findAvailablePort();
        this.redisServer = new RedisServer(redisPort);
        redisServer.start();
        System.setProperty("spring.data.redis.port", String.valueOf(redisPort)); // 테스트에 반영
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
