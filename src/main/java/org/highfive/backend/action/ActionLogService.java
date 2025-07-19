package org.highfive.backend.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public void saveLog(ActionLog actionLog) {
        actionLogRepository.save(actionLog);
    }
}
