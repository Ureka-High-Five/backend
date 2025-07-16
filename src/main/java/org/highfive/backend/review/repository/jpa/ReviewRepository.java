package org.highfive.backend.review.repository.jpa;

import org.highfive.backend.review.repository.querydsl.ReviewQueryRepository;
import org.highfive.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Optional<Review> findByUserIdAndContentId(Long userId, Long contentId);
}
