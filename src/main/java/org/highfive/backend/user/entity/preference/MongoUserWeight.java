package org.highfive.backend.user.entity.preference;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_weight")
public class MongoUserWeight {

    @Id
    private String id;

    private Long metaInfoId;

    private Long userId;

    private String name;

    private double weight;

    private String type;
}
