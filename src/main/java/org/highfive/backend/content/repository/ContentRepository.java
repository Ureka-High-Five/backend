package org.highfive.backend.content.repository;

import java.util.List;
import java.util.Map;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query(value = """
        SELECT *
        FROM (
          SELECT c.id, c.post_url, c.title, c.popularity, m.name,
              ROW_NUMBER() OVER (PARTITION BY m.name ORDER BY c.popularity DESC) AS rn
          FROM contents c
          JOIN meta_info_contents mic ON mic.content_id = c.id
          JOIN meta_info m ON m.id = mic.meta_info_id
          WHERE m.type = 'GENRE'
        ) ranked
        WHERE rn = 1
        ORDER BY popularity DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MostPopularContentPerGenreDto> findTopContentPerGenre(@Param("limit") int limit);

    @Query("SELECT new map(c.id as contentId, m.name as genreName) " +
            "FROM MetaInfoContents mic " +
            "JOIN mic.metaInfo m " +
            "JOIN mic.content c " +
            "WHERE m.type = 'GENRE' AND c.id IN :contentIds")
    List<Map<String, Object>> findContentGenresByContentIds(@Param("contentIds") List<Long> contentIds);

    @Query(value = """
    SELECT c.id, c.post_url
    FROM contents c
    JOIN meta_info_contents mic ON mic.content_id = c.id
    JOIN meta_info m ON m.id = mic.meta_info_id
    WHERE m.type = 'GENRE'
      AND m.name = :genre
    ORDER BY c.popularity DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<TopContentsByGenreDto> findTopContentsByGenre(
            String genre,
            int limit
    );

    @Query(value = """
        SELECT * FROM contents
        WHERE title LIKE :input
          AND (:cursor IS NULL OR id < :cursor)
        ORDER BY id DESC
    """, nativeQuery = true)
    List<Content> searchByInput(
            @Param("input") String input,
            @Param("cursor") String cursor,
            Pageable pageable
    );
}
