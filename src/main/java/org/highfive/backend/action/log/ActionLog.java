package org.highfive.backend.action.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.highfive.backend.action.Action;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "action_log")
public class ActionLog {

    @Id
    private String id;

    @Indexed
    private long userId;

    private long contentId;

    private Action action;

    private double value;

    private long timestamp;

    private MetaInfoLog metaInfo;

    private ActionLogStatus status;

    public void updateMetaInfo(final MetaInfoLog metaInfoLog) {
        this.metaInfo = metaInfoLog;
    }

    public void updateStatus(final ActionLogStatus status) {
        this.status = status;
    }
}
