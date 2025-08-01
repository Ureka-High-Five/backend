package org.highfive.backend.content.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.highfive.backend.global.entity.BaseEntity;

@Entity
@Builder
@Table(name = "contents_vector")
@NoArgsConstructor
@AllArgsConstructor
public class ContentVector extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String embedding;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    public void updateEmbedding(final String embedding) {
        this.embedding = embedding;
    }

    public void updateContent(Content content) {
        this.content = content;
        content.updateContentVector(this);
    }
}
