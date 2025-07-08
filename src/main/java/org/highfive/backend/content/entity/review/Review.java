package org.highfive.backend.content.entity.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.user.entity.User;

@Entity
@Getter
@Table(name = "reviews")
@Builder
@AllArgsConstructor
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private String reviewText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Builder
    private Review(int rating, String reviewText, User user, Content content) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.user = user;
        this.content = content;
    }

    public static Review of(int rating, String reviewText, User user, Content content) {
        return Review.builder()
                .rating(rating)
                .reviewText(reviewText)
                .user(user)
                .content(content)
                .build();
    }

    public void updateReview(int rating, String reviewText) {
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public boolean isWrittenBy(Long userId) {
        return Objects.equals(this.user.getId(), userId);
    }
}
