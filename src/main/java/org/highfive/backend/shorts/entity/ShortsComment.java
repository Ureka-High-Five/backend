package org.highfive.backend.shorts.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.highfive.backend.global.entity.BaseEntity;
import org.highfive.backend.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Long time;

    public static ShortsComment of(User user, Shorts shorts, String message, Long time){
        return ShortsComment.builder()
                .user(user)
                .shorts(shorts)
                .message(message)
                .time(time)
                .build();
    }
}
