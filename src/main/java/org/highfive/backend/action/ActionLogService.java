package org.highfive.backend.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public void saveLog(ActionLog actionLog) {
        log.info("ActionLog = {}", actionLog.toString());
//        actionLogRepository.save(actionLog);
    }
}
