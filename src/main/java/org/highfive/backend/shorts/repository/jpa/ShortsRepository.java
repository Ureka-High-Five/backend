package org.highfive.backend.shorts.repository.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.highfive.backend.shorts.dto.ShortsDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {

    @Modifying
    @Query("update Shorts s set s.likeCount = s.likeCount + 1 where s.id = :id")
    void increaseLike(Long id);

    @Modifying
    @Query("update Shorts s set s.likeCount = s.likeCount - 1 where s.id = :id and s.likeCount > 0")
    void decreaseLike(Long id);

    @Query("SELECT s FROM Shorts s WHERE s.content.id = :contentId ORDER BY function('RANDOM')")
    Optional<Shorts> findRandomByContentId(@Param("contentId") Long contentId);

    @Query(value = """
            SELECT 
                s.id AS shortsId,
                s.shorts_url AS shortsUrl,
                s.thumbnail_url AS shortsThumbnail,
                con.id AS contentId,
                con.title AS contentTitle
            FROM shorts s
            JOIN contents con ON s.content_id = con.id
            WHERE s.content_id NOT IN (:contentsId)
            ORDER BY random()
            LIMIT :limit
            """, nativeQuery = true)
    List<ShortsDto> findRandomShortsExcludingContentIds(
            List<Long> contentsId,
            int limit
    );

    @Query(value = """
            SELECT 
                s.id AS shortsId,
                s.shorts_url AS shortsUrl,
                s.thumbnail_url AS shortsThumbnail,
                con.id AS contentId,
                con.title AS contentTitle
            FROM shorts s
            JOIN contents con ON s.content_id = con.id
            WHERE con.id IN (
                SELECT con.id
                FROM contents con
                JOIN contents_vector cv ON con.id = cv.content_id
                JOIN users_vector u ON u.user_id = :userId
                ORDER BY cv.embedding <#> u.embedding
                LIMIT :count
            )
            """, nativeQuery = true)
    List<ShortsDto> findRecommendedShortsByUser(
            Long userId,
            int count
    );
}
