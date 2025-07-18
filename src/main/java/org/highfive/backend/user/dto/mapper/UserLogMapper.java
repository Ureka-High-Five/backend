package org.highfive.backend.user.dto.mapper;

import org.highfive.backend.content.entity.Content;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.log.ActionType;
import org.highfive.backend.user.entity.log.UserLog;

public class UserLogMapper {
    public static UserLog toUserWatchLog(final User user, final Content content, final int rating){
        return UserLog.builder()
                .user(user)
                .content(content)
                .actionType(ActionType.VIEW)
                .rating(rating)
                .build();
    }
}
