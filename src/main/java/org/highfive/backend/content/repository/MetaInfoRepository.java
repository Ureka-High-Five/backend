package org.highfive.backend.content.repository;

import java.util.List;

import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {

    @Query("SELECT m FROM MetaInfo m WHERE m.name = :name AND m.type = 'GENRE'")
    List<MetaInfo> findGenreMetaIdByName(String name);
}
