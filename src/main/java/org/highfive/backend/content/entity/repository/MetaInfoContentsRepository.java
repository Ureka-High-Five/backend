package org.highfive.backend.content.entity.repository;

import java.util.List;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MetaInfoContentsRepository extends JpaRepository<MetaInfoContents, Long> {

    List<MetaInfoContents> findByContent_Id(@Param("contentId") Long contentId);
}
