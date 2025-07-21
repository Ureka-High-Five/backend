package org.highfive.backend.rabbitmq.producer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class RecommendationMessageProducerIntegrationTest {

    @Autowired
    private RecommendationMessageProducer producer;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("메시지 전송 3번 재시도 후, recover 메서드가 호출 됩니다.")
    void givenMessageSendFails_threeTimes_thenRecoverIsCalled() {
        // when
        doThrow(new RuntimeException("MQ down"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString());

        producer.sendWeightUpdateMessage("value");

        // then
        verify(rabbitTemplate, times(3)).convertAndSend(anyString(), eq("value"));
    }
}