package org.highfive.backend.rabbitmq.dto.mapper;

import java.util.List;
import org.highfive.backend.rabbitmq.dto.UserWeightUpdateMessageDto;

public class MessageMapper {

    public static UserWeightUpdateMessageDto toUserWeightUpdateMessageDto(Long userId, List<Long> metaInfoIds, List<String> metaInfoNames, double weight){
        return new UserWeightUpdateMessageDto(userId, metaInfoIds, metaInfoNames, weight);
    }
}
