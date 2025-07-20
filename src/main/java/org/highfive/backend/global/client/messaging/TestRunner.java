package org.highfive.backend.global.client.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final RecommendationMessageProducer producer;

    @Override
    public void run(String... args) {
        producer.sendWeightUpdateMessage("user-1234");
    }
}
