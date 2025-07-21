package org.highfive.backend.review.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.review.dto.response.QReviewSimpleResponseDto;
import org.highfive.backend.review.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.review.entity.QReview;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QReview review = QReview.review;

    @Override
    public CursorPageResponse<ReviewSimpleResponseDto> findReviewsByCursor(final Long contentId, final String cursor,
                                                                           final int size, final User user) {

        final List<ReviewSimpleResponseDto> items = queryFactory
                .select(new QReviewSimpleResponseDto(review))
                .from(review)
                .where(
                        review.content.id.eq(contentId),
                        review.user.id.ne(user.getId()),
                        cursorFilter(cursor)
                )
                .orderBy(review.id.desc())
                .limit(size + 1)
                .fetch();

        final boolean hasNext = items.size() > size;
        final List<ReviewSimpleResponseDto> pageItems = subLastPage(items, hasNext);
        final String nextCursor = hasNext ? String.valueOf(pageItems.get(pageItems.size() - 1).reviewId()) : null;

        return new CursorPageResponse<>(pageItems, hasNext, nextCursor);
    }

    private BooleanExpression cursorFilter(String cursor) {
        if (Objects.isNull(cursor) || cursor.isBlank()) {
            return null;
        }

        return review.id.lt(Long.parseLong(cursor));
    }

    private List<ReviewSimpleResponseDto> subLastPage(List<ReviewSimpleResponseDto> items, boolean hasNext) {
        if (Objects.isNull(items) || items.isEmpty()) {
            return Collections.emptyList();
        }

        return hasNext ? items.subList(0, items.size() - 1) : items;
    }
}
