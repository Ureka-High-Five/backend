package org.highfive.backend.content.entity.metadata;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetaInfo {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MetaType type;

    @Builder.Default
    @OneToMany(mappedBy = "metaInfo")
    private List<MetaInfoContents> metaInfoContents = new ArrayList<>();
}
