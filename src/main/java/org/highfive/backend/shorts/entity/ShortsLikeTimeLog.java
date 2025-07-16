package org.highfive.backend.shorts.entity;

import jakarta.persistence.*;
import lombok.*;
import org.highfive.backend.global.entity.BaseEntity;
import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsLikeTimeLog extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    private Long time;
}
