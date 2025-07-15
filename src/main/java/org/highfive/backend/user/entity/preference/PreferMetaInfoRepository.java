package org.highfive.backend.user.entity.preference;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PreferMetaInfoRepository extends JpaRepository<PreferMetaInfo, Long> {

    @Query(value = """
    SELECT mi.name
    FROM prefer_meta_info pmi
    JOIN meta_info mi ON pmi.meta_info_id = mi.id
    WHERE pmi.user_id = :userId
      AND mi.type = 'GENRE'
    ORDER BY pmi.weight DESC
    LIMIT :count
""", nativeQuery = true)
    List<String> findPreferGenresByUser(Long userId, int count);

    @Query(value = """
    SELECT pmi
    FROM PreferMetaInfo pmi
    WHERE pmi.metaInfo.id = :metaInfoId AND pmi.user.id = :id
""")
    Optional<PreferMetaInfo> findByMetaInfoAndUser(Long metaInfoId, Long id);
}
