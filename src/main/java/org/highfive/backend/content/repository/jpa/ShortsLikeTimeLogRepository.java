package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.shorts.log.ShortsLikeTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsLikeTimeLogRepository extends JpaRepository<ShortsLikeTimeLog, Long> {

    boolean existsByUserIdAndShortsId(Long userId, Long shortsId);
}
