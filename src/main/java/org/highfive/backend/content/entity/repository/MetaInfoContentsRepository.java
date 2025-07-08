package org.highfive.backend.content.entity.repository;

import java.util.List;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetaInfoContentsRepository extends JpaRepository<MetaInfoContents, Long> {

    @Query("""
                select mi from MetaInfoContents mic join fetch mic.metaInfo mi where mic.content.id = :contentId
            """)
    List<MetaInfo> findMetaInfosByContentId(@Param("contentId") Long contentId);
}
