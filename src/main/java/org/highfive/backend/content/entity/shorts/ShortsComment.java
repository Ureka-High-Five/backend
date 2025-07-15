package org.highfive.backend.content.entity.shorts;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.highfive.backend.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsComment {

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

    @CreatedDate
    private LocalDateTime createdAt;

    public static ShortsComment of(User user, Shorts shorts, String message, Long time){
        return ShortsComment.builder()
                .user(user)
                .shorts(shorts)
                .message(message)
                .time(time)
                .build();
    }
}
