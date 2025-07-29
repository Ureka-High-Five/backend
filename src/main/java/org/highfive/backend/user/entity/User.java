package org.highfive.backend.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.highfive.backend.global.entity.BaseEntity;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.highfive.backend.user.entity.preference.PreferMetaInfo;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "users")
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(columnDefinition = "TEXT")
    private String embedding;

    @Column(nullable = false)
    private String email;

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

    @Column(nullable = false, unique = true)
    private String kakaoUserId;

    @CreatedDate
    private LocalDateTime createdAt;

    public void updateBasicInfo(final String name, final int age, final Gender gender, final UserRole userRole) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.userRole = userRole;
    }

    public void updateUserRole(final UserRole role) {
        this.userRole = role;
    }

    public void updateUserWatchInfo(final long averageViewTime, final long viewCount) {
        this.averageViewTime = averageViewTime;
        this.viewCount = viewCount;
    }

    public void updateUserAverageRating(final float averageRating) {
        this.averageRating = averageRating;
    }

}
