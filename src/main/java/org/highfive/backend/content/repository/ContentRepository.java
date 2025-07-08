package org.highfive.backend.content.repository;

import java.util.List;
import org.highfive.backend.content.dto.TopContentByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content,Long> {

    @Query(value = """
        SELECT *
        FROM (
          SELECT c.id, c.title, c.popularity, m.name,
              ROW_NUMBER() OVER (PARTITION BY m.name ORDER BY c.popularity DESC) AS rn
          FROM content c
          JOIN meta_info_contents mic ON mic.content_id = c.id
          JOIN meta_info m ON m.id = mic.meta_info_id
          WHERE m.type = 'GENRE'
        ) ranked
        WHERE rn = 1
        LIMIT :limit
        """, nativeQuery = true)
    List<TopContentByGenreDto> findTopContentPerGenre(@Param("limit") int limit);
}
