package org.highfive.backend.rabbitmq.dto.mapper;

import java.util.List;
import org.highfive.backend.action.Action;
import org.highfive.backend.rabbitmq.dto.UserWeightUpdateMessageDto;

public class MessageMapper {

    public static UserWeightUpdateMessageDto toUserWeightUpdateMessageDto(Long userId, List<Long> metaInfoIds, List<String> metaInfoNames, Action actionType, double value){
        return new UserWeightUpdateMessageDto(userId, metaInfoIds, metaInfoNames, actionType, value);
    }
}
