package org.highfive.backend.common.fixture;

import org.highfive.backend.content.entity.Content;
import org.highfive.backend.review.entity.Review;
import org.highfive.backend.user.entity.User;

public class ReviewFixture {

    public static Review createReview(Long id, User user, Content content){
        return Review.builder()
                .id(id)
                .user(user)
                .content(content)
                .rating(4)
                .reviewText("좋아요")
                .build();
    }
}
