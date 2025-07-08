package org.highfive.backend.content.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.QReviewSimpleResponseDto;
import org.highfive.backend.content.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.content.entity.review.QReview;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QReview review = QReview.review;

    @Override
    public CursorPageResponse<ReviewSimpleResponseDto> findReviewsByCursor(Long contentId, String cursor, int size) {

        List<ReviewSimpleResponseDto> items = queryFactory
                .select(new QReviewSimpleResponseDto(review))
                .from(review)
                .where(
                        review.content.id.eq(contentId),
                        cursorFilter(cursor)
                )
                .orderBy(review.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = items.size() > size;
        List<ReviewSimpleResponseDto> pageItems = subLastPage(items, hasNext);
        String nextCursor = getNextCursor(pageItems, hasNext);

        return new CursorPageResponse<>(pageItems, nextCursor);
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

    private String getNextCursor(List<ReviewSimpleResponseDto> items, boolean hasNext) {
        if (Objects.isNull(items) || items.isEmpty() || !hasNext) {
            return null;
        }

        return String.valueOf(items.getLast().reviewId());
    }


}
