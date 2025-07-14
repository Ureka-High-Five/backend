package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {
}
