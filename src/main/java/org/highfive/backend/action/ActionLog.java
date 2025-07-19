package org.highfive.backend.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "actionLogs")
public class ActionLog {

    @Id
    private String id;

    @Indexed
    private long userId;

    private long contentId;

    private Action action;

    private long timestamp;
}
