package org.highfive.backend.shorts.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.shorts.dto.mapper.ShortsCommentMapper;
import org.highfive.backend.shorts.dto.response.ShortsCommentsByIdResponseDto;
import org.highfive.backend.shorts.entity.QShortsComment;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShortsCommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QShortsComment shortsComment = QShortsComment.shortsComment;

    public CursorPageResponse<ShortsCommentsByIdResponseDto> findByIdAndCursor(Long shortsId, long cursor, int size) {
        List<ShortsComment> findComments = jpaQueryFactory
                .selectFrom(shortsComment)
                .where(
                        shortsComment.shorts.id.eq(shortsId),
                        cursorFilter(String.valueOf(cursor))
                ).orderBy(shortsComment.id.desc())
                .limit(size + 1)
                .fetch();
        boolean hasNext = findComments.size() > size;
        Long nextCursor = hasNext ? findComments.getLast().getId() : null;
        List<ShortsCommentsByIdResponseDto> result = findComments.stream()
                .map(ShortsCommentMapper::toShortsCommentsByIdResponseDto)
                .limit(size)
                .toList();
        return new CursorPageResponse<>(result, hasNext, String.valueOf(nextCursor));
    }

    private BooleanExpression cursorFilter(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return shortsComment.id.goe(Long.parseLong(cursor));
    }
}
