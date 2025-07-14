package org.highfive.backend.curation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.highfive.backend.content.entity.Content;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationContents {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curation_id")
    private Curation curation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    public void assignCuration(final Curation curation) {
        this.curation = curation;
    }
}
