package org.highfive.backend.content.repository;

import org.highfive.backend.content.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
