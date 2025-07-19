package org.highfive.backend.global.client.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${queue.name}")
    private String QUEUE_NAME;

    @Bean
    public Queue updateWeightQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}
