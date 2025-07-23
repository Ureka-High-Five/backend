package org.highfive.backend.shorts.entity;

import jakarta.persistence.*;
import lombok.*;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shorts extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(nullable = false)
    private String shortsUrl;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private int runningTime;

    @Builder.Default
    @OneToMany(mappedBy = "shorts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortsLikeTimeLog> shortsLikeTimeLogs = new ArrayList<>();
}
