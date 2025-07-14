package org.highfive.backend.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import org.hibernate.annotations.DynamicUpdate;
import org.highfive.backend.content.entity.metadata.Episode;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.Series;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "contents")
@Builder
@AllArgsConstructor
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Content {

    public void updateEmbedding(String embedding) {
        this.embedding = embedding;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, length = 500)
    private String videoUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String postUrl;

    @Column(nullable = false)
    private LocalDateTime openDate;

    private int runningTime;

    private int totalRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(nullable = false)
    private String embedding;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false)
    private int popularity;

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<Episode> episodes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<Shorts> shorts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "content")
    private List<MetaInfoContents> metaInfoContents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    private LocalDateTime deletedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
