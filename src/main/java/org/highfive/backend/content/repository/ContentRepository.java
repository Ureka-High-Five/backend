package org.highfive.backend.content.repository;

import java.util.List;
import java.util.Map;
import org.highfive.backend.content.dto.TopContentByGenreDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
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

    @Query(value = """
        SELECT m.name
        FROM meta_info_content mic
        JOIN meta_info m ON mic.meta_info_id = m.id
        WHERE mic.content_id = :contentId
          AND m.type = 'GENRE'
        """, nativeQuery = true)
    List<String> findGenreNamesByContentId(@Param("contentId") Long contentId); // 컨텐츠 아이디로 장르 조회


    @Query("SELECT new map(c.id as contentId, m.name as genreName) " +
            "FROM MetaInfoContents mic " +
            "JOIN mic.metaInfo m " +
            "JOIN mic.content c " +
            "WHERE m.type = 'GENRE' AND c.id IN :contentIds")
    List<Map<String, Object>> findContentGenresByContentIds(@Param("contentIds") List<Long> contentIds);

    @Query("""
    SELECT new org.highfive.backend.content.dto.response.OnboardingContentDto(
        c.id, c.postUrl, c.title, c.openDate
    )
    FROM Content c
    JOIN MetaInfoContents mic ON c.id = mic.content.id
    JOIN MetaInfo m ON mic.metaInfo.id = m.id
    WHERE m.type = 'GENRE'
      AND m.name IN :genres
    GROUP BY c.id, c.postUrl, c.title, c.openDate
    HAVING COUNT(DISTINCT m.name) = :genreCount
    """)
    List<OnboardingContentDto> findContentsByGenres(
            @Param("genres") List<String> genres,
            @Param("genreCount") long genreCount
    );

    @Query("""
    SELECT new org.highfive.backend.content.dto.response.OnboardingContentDto(
        c.id, c.postUrl, c.title, c.openDate
    )
    FROM Content c
    JOIN MetaInfoContents mic ON c.id = mic.content.id
    JOIN MetaInfo m ON mic.metaInfo.id = m.id
    WHERE m.type = 'GENRE'
    GROUP BY c.id, c.postUrl, c.title, c.openDate
    HAVING COUNT(DISTINCT m.name) = 1 AND MAX(m.name) = :genre
""")
    List<OnboardingContentDto> findContentsWithOnlyOneGenre(
            @Param("genre") String genre
    );

}
