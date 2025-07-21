package org.highfive.backend.review.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.review.entity.Review;
import org.highfive.backend.review.repository.querydsl.ReviewQueryRepository;
import org.highfive.backend.user.dto.response.RatedContentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Optional<Review> findByUserIdAndContentId(Long userId, Long contentId);

    @Query(value = """
            SELECT
                r.id as reviewId,
                c.thumbnail_url as thumbnailUrl,
                c.title,
                r.review_text as reviewText,
                r.rating
            FROM reviews r
            JOIN contents c ON r.content_id = c.id
            WHERE r.user_id = :userId
              AND (:cursor IS NULL OR r.id < :cursor)
            ORDER BY r.id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<RatedContentResponseDto> findByUser(
            Long userId,
            String cursor,
            int limit
    );

    boolean existsByUserIdAndContentId(Long userId, Long contentId);
}
