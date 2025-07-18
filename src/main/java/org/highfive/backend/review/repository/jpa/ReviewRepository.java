package org.highfive.backend.review.repository.jpa;

import org.highfive.backend.review.entity.Review;
import org.highfive.backend.review.repository.querydsl.ReviewQueryRepository;
import org.highfive.backend.user.dto.response.RatedContentResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Optional<Review> findByUserIdAndContentId(Long userId, Long contentId);

    @Query(value = """
                SELECT
                    r.id,
                    c.thumbnailUrl,
                    c.title,
                    r.reviewText,
                    r.rating
                FROM Review r
                JOIN r.content c
                WHERE r.user.id = :userId
                  AND (:cursor IS NULL OR r.id < :cursor)
                ORDER BY r.id DESC
            """)
    List<RatedContentResponseDto> findByUser(
            @Param("userId") Long userId,
            @Param("cursor") String cursor,
            Pageable pageable
    );
}
