package org.highfive.backend.shorts.repository.jpa;

import org.highfive.backend.shorts.entity.ShortsLikeTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortsLikeTimeLogRepository extends JpaRepository<ShortsLikeTimeLog, Long> {

    boolean existsByUserIdAndShortsId(Long userId, Long shortsId);

    Optional<ShortsLikeTimeLog> findByUserIdAndShortsId(Long userId, Long shortsId);
}
