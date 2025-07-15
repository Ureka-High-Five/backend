package org.highfive.backend.content.repository;

import java.util.List;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MetaInfoContentsRepository extends JpaRepository<MetaInfoContents, Long> {

    List<MetaInfoContents> findByContentId(Long contentId);

    @Query("DELETE FROM MetaInfoContents mic WHERE mic.content.id = :contentId AND mic.metaInfo.type = :metaType")
    void deleteAllByContentAndType(Long contentId, MetaType metaType);
}
