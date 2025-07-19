package org.highfive.backend.global.client.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.name}")
    private String queueName;

    public void sendWeightUpdateMessage(String userId) {
        rabbitTemplate.convertAndSend(queueName, userId);
        System.out.println("메시지 전송 " + userId);
    }
}
