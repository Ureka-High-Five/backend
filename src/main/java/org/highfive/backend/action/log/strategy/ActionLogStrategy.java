package org.highfive.backend.action.log.strategy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.highfive.backend.action.log.ActionLog;

public interface ActionLogStrategy {

    ActionLog createLog(ProceedingJoinPoint joinPoint, long userId, long timestamp);
}
