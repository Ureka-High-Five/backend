package org.highfive.backend.shorts.repository.jpa;

import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsLikeTimeLogRepository extends JpaRepository<ShortsLikeTimeLog, Long> {

    boolean existsByUserIdAndShortsId(Long userId, Long shortsId);
}
