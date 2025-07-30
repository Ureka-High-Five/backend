package org.highfive.backend.content.repository.jpa;

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
          SELECT c.id, c.thumbnail_url, c.title, c.popularity, m.name,
              ROW_NUMBER() OVER (PARTITION BY m.name ORDER BY c.popularity DESC) AS rn, CAST(EXTRACT(YEAR FROM c.open_date) AS INTEGER) AS open_date
          FROM contents c
          JOIN meta_info_contents mic ON mic.content_id = c.id
          JOIN meta_info m ON m.id = mic.meta_info_id
          WHERE m.type = 'GENRE' AND c.deleted_at IS NULL
        ) ranked
        WHERE rn = 1
        ORDER BY popularity DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MostPopularContentPerGenreDto> findTopContentPerGenre(@Param("limit") int limit);

    @Query("""
    SELECT new map(c.id as contentId, m.name as genreName)
    FROM MetaInfoContents mic
    JOIN mic.metaInfo m
    JOIN mic.content c
    WHERE m.type = 'GENRE'
      AND c.id IN :contentIds
      AND c.deletedAt IS NULL
    """)
    List<Map<String, Object>> findContentGenresByContentIds(@Param("contentIds") List<Long> contentIds);

    @Query(value = """
    SELECT c FROM Content c
    WHERE c.title LIKE :input
    AND (:cursor IS NULL OR c.id < :cursor)
    AND c.deletedAt IS NULL
    ORDER BY c.id DESC
    """)
    List<Content> searchByInput(
            String input,
            Long cursor,
            Pageable pageable
    );

    @Query(value = """
        SELECT * FROM (
            SELECT c.*
            FROM contents c
            JOIN contents_vector cv ON c.id = cv.content_id
            JOIN users_vector u ON u.user_id = :userId
            WHERE c.deleted_at IS NULL
            ORDER BY cv.embedding <#> u.embedding
            LIMIT 30
        ) AS similar_contents
        ORDER BY random()
        LIMIT :count
    """, nativeQuery = true)
    List<Content> findRecommendedContentsByUser(
            Long userId,
            int count
    );
}