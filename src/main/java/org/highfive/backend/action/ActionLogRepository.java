package org.highfive.backend.action;

import org.highfive.backend.action.log.ActionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActionLogRepository extends MongoRepository<ActionLog, String> {
}
