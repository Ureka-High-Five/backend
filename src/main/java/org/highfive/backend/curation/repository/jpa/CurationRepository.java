package org.highfive.backend.curation.repository.jpa;

import java.util.List;
import org.highfive.backend.curation.entity.Curation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    @Query("""
            SELECT c FROM Curation c
            JOIN FETCH c.user
            WHERE c.user.id <> :userId
            ORDER BY FUNCTION('RANDOM')
            """)
    List<Curation> findRandomCurationsExcludeUser(@Param("userId") Long userId, Pageable pageable);
}
