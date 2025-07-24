package org.highfive.backend.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.action.ActionLogRepository;
import org.highfive.backend.action.exception.ActionLogErrorCode;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.action.log.ActionLogStatus;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.rabbitmq.dto.UserWeightUpdateMessageDto;
import org.highfive.backend.rabbitmq.exception.MessageDeliveryException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationMessageProducer {

    private final String MANAGED_ACTION_LOG = "managed_action_log";

    private final RabbitTemplate rabbitTemplate;
    private final ActionLogRepository actionLogRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${queue.name}")
    private String queueName;

    @Retryable(
            retryFor = {MessageDeliveryException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200)
    )
    public void sendWeightUpdateMessage(final UserWeightUpdateMessageDto message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
        } catch (Exception e) {
            throw new MessageDeliveryException("메시지 전송 실패 ", e);
        }
    }

    @Recover
    public void recover(final MessageDeliveryException e, final UserWeightUpdateMessageDto message) {
        log.error("메시지 전송 실패, id={}, error={}", message.id(), e.getMessage(), e);
        if(message.id() != null) {
            try {
                final ActionLog actionLog = actionLogRepository.findById(message.id()).orElseThrow(() -> new BusinessException(ActionLogErrorCode.ACTION_LOG_NOT_FOUND));
                actionLog.updateStatus(ActionLogStatus.FAIL);
                actionLogRepository.save(actionLog);
                mongoTemplate.save(actionLog, MANAGED_ACTION_LOG);
            } catch (Exception ex) {
                log.error("[actionlog_status_update_failed] ActionLog 상테 FAIL 업데이트 중 예외 발생 : id = {}, error = {}", message.id(), ex.getMessage(), ex);
            }
        }
    }
}