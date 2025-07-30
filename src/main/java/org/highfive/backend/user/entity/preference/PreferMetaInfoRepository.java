package org.highfive.backend.user.entity.preference;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreferMetaInfoRepository extends JpaRepository<PreferMetaInfo, Long> {

    @Query("""
        SELECT mi.name
        FROM PreferMetaInfo pmi
        JOIN pmi.metaInfo mi
        WHERE pmi.user.id = :userId
          AND mi.type = 'GENRE'
        ORDER BY pmi.weight DESC
    """)
    List<String> findPreferGenresByUser(Long userId, Pageable pageable);
}
