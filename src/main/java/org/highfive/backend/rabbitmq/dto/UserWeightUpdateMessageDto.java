package org.highfive.backend.rabbitmq.dto;

import java.util.List;
import org.highfive.backend.action.Action;

public record UserWeightUpdateMessageDto(
        String id,
        Long userId,
        List<Long> metaInfoIds,
        List<String> metaInfoNames,
        Action actionType,
        double value
) {
}