package org.highfive.backend.action;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.rabbitmq.dto.UserWeightUpdateMessageDto;
import org.highfive.backend.rabbitmq.dto.mapper.MessageMapper;
import org.highfive.backend.rabbitmq.producer.RecommendationMessageProducer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final RecommendationMessageProducer producer;
    private final MetaInfoContentsRepository metaInfoContentsRepository;

    public void saveLog(final ActionLog actionLog) {
        log.info("ActionLog = {}", actionLog.toString());
        actionLogRepository.save(actionLog);
    }

    public void publishUpdateWeightMessage(final ActionLog actionLog){
        List<MetaInfoContents> metaInfoContents = metaInfoContentsRepository.findByContentId(actionLog.getContentId());
        List<Long> metaInfoIds = extractMetaInfoIds(metaInfoContents);
        List<String> metaInfoName = extractMetaInfoNames(metaInfoContents);

        UserWeightUpdateMessageDto message = MessageMapper.toUserWeightUpdateMessageDto(actionLog.getUserId(),metaInfoIds,metaInfoName,actionLog.getAction(),actionLog.getValue());

        producer.sendWeightUpdateMessage(message);
    }

    private List<Long> extractMetaInfoIds(final List<MetaInfoContents> metaInfoContents){
        return metaInfoContents.stream()
                .map(meta -> meta.getMetaInfo().getId())
                .toList();
    }

    private List<String> extractMetaInfoNames(final List<MetaInfoContents> metaInfoContents){
        return metaInfoContents.stream()
                .map(meta -> meta.getMetaInfo().getName())
                .toList();
    }
}
