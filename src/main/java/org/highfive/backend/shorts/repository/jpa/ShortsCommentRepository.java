package org.highfive.backend.shorts.repository.jpa;

import java.util.Optional;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsCommentRepository extends JpaRepository<ShortsComment, Long> {

    Optional<ShortsComment> findFirstByShortsIdAndTimeOrderByCreatedAtDesc(Long shortsId, Long time);
}
