package org.highfive.backend.content.entity.shorts;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shorts {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder.Default
    @OneToMany(mappedBy = "shorts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortsLikeTimeLog> shortsLikeTimeLogs = new ArrayList<>();
}
