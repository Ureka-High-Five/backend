package org.highfive.backend.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.highfive.backend.content.entity.shorts.ShortsComment;
import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;
import org.highfive.backend.user.entity.preference.PreferMetaInfo;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(nullable = false)
    private String profileUrl;

    private Long averageViewTime;

    private String embedding;

    private float averageRating;

    private Long viewCount;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<PreferMetaInfo> preferMetaInfos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<ShortsComment> shortsComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<ShortsLikeTimeLog> shortsLikeTimeLogs = new ArrayList<>();

    @Column(nullable = false)
    private String kakaoUserId;

    @CreatedDate
    private LocalDateTime createdAt;

    public void updateBasicInfo(final String name, final int age, final Gender gender, final UserRole userRole) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.userRole = userRole;
    }

    public void updateEmbedding(final String embedding) {
        this.embedding = embedding;
    }

}
