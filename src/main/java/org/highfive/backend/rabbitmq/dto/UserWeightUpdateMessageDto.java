package org.highfive.backend.rabbitmq.dto;

import java.util.List;

public record UserWeightUpdateMessageDto(
        Long userId,
        List<Long> metaInfoIds,
        List<String> metaInfoNames,
        double weight
) {
}