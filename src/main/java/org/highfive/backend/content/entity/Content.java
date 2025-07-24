package org.highfive.backend.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.highfive.backend.content.dto.request.AdminUpdateContentRequestDto;
import org.highfive.backend.global.entity.BaseEntity;
import org.highfive.backend.metadata.entity.Episode;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.Series;
import org.highfive.backend.shorts.entity.Shorts;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "contents")
@Builder
@AllArgsConstructor
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1024)
    private String description;

    @Column(nullable = false, length = 1024)
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

    @Column(nullable = false, columnDefinition = "TEXT")
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

    public void updateEmbedding(String embedding) {
        this.embedding = embedding;
    }

    public Content updateFromDto(AdminUpdateContentRequestDto request) {
        this.title = request.title();
        this.description = request.description();
        this.videoUrl = request.videoUrl();
        this.thumbnailUrl = request.thumbnailUrl();
        this.postUrl = request.postUrl();
        this.openDate = LocalDate.parse(request.openDate()).atStartOfDay();
        this.runningTime = request.runningTime();
        this.totalRound = request.totalRound();
        this.grade = request.grade();

        return this;
    }

    public boolean delete() {
        if (deletedAt != null) {
            return false;
        }
        this.deletedAt = LocalDateTime.now();
        return true;
    }
}
