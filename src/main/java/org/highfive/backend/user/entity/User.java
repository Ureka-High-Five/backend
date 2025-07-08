package org.highfive.backend.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
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

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

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

    public static User from(final KakaoUserResponseDto response) {
        final KakaoUserResponseDto.KakaoProfile profile = response.kakaoAccount().profile();
        return User.builder()
                .kakaoUserId(response.id())
                .name(profile.nickname())
                .profileUrl(profile.profileImageUrl())
                .build();
    }

    public void updateBasicInfo(String name, int age, Gender gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public void updateEmbedding(String embedding) {
        this.embedding = embedding;
    }

}
