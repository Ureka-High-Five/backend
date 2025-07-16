package org.highfive.backend.shorts.repository.jpa;

import org.highfive.backend.shorts.entity.ShortsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsCommentRepository extends JpaRepository<ShortsComment,Long> {
}
