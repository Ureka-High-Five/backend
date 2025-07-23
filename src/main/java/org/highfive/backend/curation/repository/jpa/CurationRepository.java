package org.highfive.backend.curation.repository.jpa;

import java.util.List;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.CurationDto;
import org.highfive.backend.curation.entity.Curation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    @Query("""
        SELECT c FROM Curation c
        JOIN FETCH c.user
        ORDER BY FUNCTION('RANDOM')
    """)
    List<Curation> findRandomCurations();
}
