package org.highfive.backend.content.repository;

import java.util.Optional;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.review.Review;
import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    Optional<Review> findByUserAndContent(User user, Content content);
}
