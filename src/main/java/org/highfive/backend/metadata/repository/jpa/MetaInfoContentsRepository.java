package org.highfive.backend.metadata.repository.jpa;

import java.util.List;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MetaInfoContentsRepository extends JpaRepository<MetaInfoContents, Long> {

    List<MetaInfoContents> findByContentId(Long contentId);

    @Query("DELETE FROM MetaInfoContents mic WHERE mic.content.id = :contentId AND mic.metaInfo.type = :metaType")
    void deleteAllByContentAndType(Long contentId, MetaType metaType);
}
