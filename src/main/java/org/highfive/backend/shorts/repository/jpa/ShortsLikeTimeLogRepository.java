package org.highfive.backend.shorts.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShortsLikeTimeLogRepository extends JpaRepository<ShortsLikeTimeLog, Long> {

    boolean existsByUserIdAndShortsId(Long userId, Long shortsId);

    @Query("SELECT s.shorts.id FROM ShortsLikeTimeLog s WHERE s.user.id = :userId AND s.shorts.id IN :shortsIds")
    List<Long> findLikedShortsIds(Long userId, List<Long> shortsIds);

    @Query(value = """
            SELECT 
                FLOOR(s.time / :duration) * :duration AS time_group,
                COUNT(*) AS count
            FROM shorts_like_time_log s
            WHERE s.shorts_id = :shortsId
            GROUP BY time_group
            ORDER BY time_group
            """, nativeQuery = true)
    List<Object[]> findAllShortsLikeWithTime(
            final Long shortsId,
            final int duration
    );

    Optional<ShortsLikeTimeLog> findByUserIdAndShortsId(Long userId, Long shortsId);

    @Query("SELECT s FROM ShortsLikeTimeLog sl JOIN sl.shorts s WHERE sl.user.id = :userId AND s.id < :cursor ORDER BY s.id DESC")
    List<Shorts> findLikedShorts(Long userId, Long cursor, Pageable pageable);
}
