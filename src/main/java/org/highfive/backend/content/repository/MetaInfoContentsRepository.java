package org.highfive.backend.content.repository;

import java.util.List;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaInfoContentsRepository extends JpaRepository<MetaInfoContents, Long> {

    List<MetaInfoContents> findByContentId(Long contentId);
}
