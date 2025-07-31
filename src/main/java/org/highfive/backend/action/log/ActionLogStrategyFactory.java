package org.highfive.backend.action.log;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.log.strategy.ActionLogStrategy;
import org.highfive.backend.action.log.strategy.ClickActionLogStrategy;
import org.highfive.backend.action.log.strategy.DislikeActionLogStrategy;
import org.highfive.backend.action.log.strategy.LikeActionLogStrategy;
import org.highfive.backend.action.log.strategy.RatingActionLogStrategy;
import org.highfive.backend.action.log.strategy.WatchActionLogStrategy;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActionLogStrategyFactory {

    private final ClickActionLogStrategy clickStrategy;
    private final WatchActionLogStrategy watchStrategy;
    private final RatingActionLogStrategy ratingStrategy;
    private final LikeActionLogStrategy likeStrategy;
    private final DislikeActionLogStrategy dislikeStrategy;
    private final Map<Action, ActionLogStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        strategyMap.put(Action.CLICK, clickStrategy);
        strategyMap.put(Action.WATCH, watchStrategy);
        strategyMap.put(Action.RATING, ratingStrategy);
        strategyMap.put(Action.LIKE, likeStrategy);
        strategyMap.put(Action.DISLIKE, dislikeStrategy);
    }

    public ActionLogStrategy getStrategy(Action action) {
        ActionLogStrategy strategy = strategyMap.get(action);
        if (strategy == null) {
            throw new BusinessException(GlobalErrorCode.BAD_REQUEST);
        }
        return strategy;
    }
}
