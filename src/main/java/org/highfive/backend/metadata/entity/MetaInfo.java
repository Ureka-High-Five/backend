package org.highfive.backend.metadata.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.highfive.backend.global.entity.BaseEntity;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetaInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meta_info_seq_generator")
    @SequenceGenerator(
            name = "meta_info_seq_generator",
            sequenceName = "meta_info_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MetaType type;

    @Builder.Default
    @OneToMany(mappedBy = "metaInfo")
    private List<MetaInfoContents> metaInfoContents = new ArrayList<>();
}
