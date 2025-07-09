package org.highfive.backend.content.repository;

import org.highfive.backend.content.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Optional<Review> findByUserIdAndContentId(Long userId, Long contentId);
}
