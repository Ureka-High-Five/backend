package org.highfive.backend.content.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.highfive.backend.content.entity.curation.CurationContents;
import org.highfive.backend.content.entity.metadata.ActorContents;
import org.highfive.backend.content.entity.metadata.Country;
import org.highfive.backend.content.entity.metadata.Episode;
import org.highfive.backend.content.entity.metadata.Series;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contents")
@Builder
@AllArgsConstructor
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Content {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String postUrl;

    @Column(nullable = false)
    private LocalDateTime openDate;

    private LocalDateTime runningTime;

    private int totalRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String embedding;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false)
    private int popularity;

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<ActorContents> actorContents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<CurationContents> curationContents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<Episode> episodes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<Shorts> shorts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    private LocalDateTime deletedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
