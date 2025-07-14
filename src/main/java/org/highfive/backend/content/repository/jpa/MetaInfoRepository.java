package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {

    @Query(" SELECT mi FROM MetaInfo mi WHERE mi.name = :name AND mi.type = :type ")
    MetaInfo findByNameAndType(@Param("name") String name, @Param("type") MetaType type);
}
