package org.highfive.backend.shorts.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsCommentRepository extends JpaRepository<ShortsComment,Long> {

    List<ShortsComment> findByShortsIdAndTimeOrderByCreatedAtDesc(long shortsId, long time);

    Optional<ShortsComment> findFirstByShortsIdAndTimeOrderByCreatedAtDesc(Long shortsId, Long time);
}
