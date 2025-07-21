package org.highfive.backend.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.rabbitmq.dto.UserWeightUpdateMessageDto;
import org.highfive.backend.rabbitmq.exception.MessageDeliveryException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.name}")
    private String queueName;

    @Retryable(
            retryFor = {MessageDeliveryException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200)
    )
    public void sendWeightUpdateMessage(UserWeightUpdateMessageDto message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
        } catch (Exception e) {
            throw new MessageDeliveryException("메시지 전송 실패 ", e);
        }
    }

    @Recover
    public void recover(MessageDeliveryException e, String value) {
        log.error("메시지 전송 실패, value={}, error={}", value, e.getMessage(), e);
    }
}
