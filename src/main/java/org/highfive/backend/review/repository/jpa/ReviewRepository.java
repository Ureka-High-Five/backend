package org.highfive.backend.review.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.review.entity.Review;
import org.highfive.backend.review.repository.querydsl.ReviewQueryRepository;
import org.highfive.backend.user.dto.response.RatedContentResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Optional<Review> findByUserIdAndContentId(Long userId, Long contentId);

    @Query("""
                SELECT new org.highfive.backend.user.dto.response.RatedContentResponseDto(
                    r.id, c.thumbnailUrl, c.title, r.reviewText, r.rating, c.id
                )
                FROM Review r
                JOIN r.content c
                WHERE r.user.id = :userId
                 AND (:cursor IS NULL OR r.id <= :cursor)
                ORDER BY r.id DESC
            """)
    List<RatedContentResponseDto> findByUser(
            Long userId,
            Long cursor,
            Pageable pageable
    );

    boolean existsByUserIdAndContentId(Long userId, Long contentId);

    List<Review> findAllByUserId(Long userId);
}
