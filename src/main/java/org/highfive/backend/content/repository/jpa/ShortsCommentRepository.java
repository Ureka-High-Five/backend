package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.shorts.ShortsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsCommentRepository extends JpaRepository<ShortsComment,Long> {
}
