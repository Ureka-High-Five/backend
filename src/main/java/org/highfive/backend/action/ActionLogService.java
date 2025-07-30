package org.highfive.backend.action;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.action.exception.ActionLogErrorCode;
import org.highfive.backend.action.log.ActionLog;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.rabbitmq.dto.UserWeightUpdateMessageDto;
import org.highfive.backend.rabbitmq.dto.mapper.MessageMapper;
import org.highfive.backend.rabbitmq.producer.RecommendationMessageProducer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final String MANAGED_ACTION_LOG = "managed_action_log";

    private final ActionLogRepository actionLogRepository;
    private final RecommendationMessageProducer producer;
    private final MetaInfoContentsRepository metaInfoContentsRepository;
    private final MongoTemplate mongoTemplate;

    public ActionLog saveLog(ActionLog actionLog) {
        log.info("ActionLog = {}", actionLog.toString());
        try {
            actionLogRepository.save(actionLog);
            actionLog = mongoTemplate.save(actionLog, MANAGED_ACTION_LOG);
        } catch (Exception ex) {
            log.error("[actionlog_save_failed] ActionLog 저장 중 예외 발생: actionLog={}, error={}", actionLog, ex.getMessage(), ex);
            throw new BusinessException(ActionLogErrorCode.ACTION_LOG_NOT_SAVE);
        }

        return actionLog;
    }

    public void publishUpdateWeightMessage(final ActionLog actionLog) {
        List<MetaInfoContents> metaInfoContents = metaInfoContentsRepository.findByContentId(actionLog.getContentId());
        List<Long> metaInfoIds = extractMetaInfoIds(metaInfoContents);
        List<String> metaInfoName = extractMetaInfoNames(metaInfoContents);
        List<String> metaInfoType = extractMetaInfoType(metaInfoContents);
        UserWeightUpdateMessageDto message = MessageMapper.toUserWeightUpdateMessageDto(actionLog.getId(), actionLog.getUserId(),
                metaInfoIds, metaInfoType, metaInfoName, actionLog.getAction(), actionLog.getValue());

        producer.sendWeightUpdateMessage(message);
    }

    private List<String> extractMetaInfoType(final List<MetaInfoContents> metaInfoContents) {
        return metaInfoContents.stream()
                .map(meta -> meta.getMetaInfo().getType().toString())
                .toList();
    }

    private List<Long> extractMetaInfoIds(final List<MetaInfoContents> metaInfoContents) {
        return metaInfoContents.stream()
                .map(meta -> meta.getMetaInfo().getId())
                .toList();
    }

    private List<String> extractMetaInfoNames(final List<MetaInfoContents> metaInfoContents) {
        return metaInfoContents.stream()
                .map(meta -> meta.getMetaInfo().getName())
                .toList();
    }
}