package org.highfive.backend.shorts.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.shorts.entity.QShorts;
import org.highfive.backend.shorts.entity.Shorts;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShortsQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QShorts shorts = QShorts.shorts;

    public List<Shorts> findByCursor(final String cursor, final int size) {
        return jpaQueryFactory
                .select(shorts)
                .from(shorts)
                .where(
                        cursorFilter(cursor)
                ).orderBy(shorts.id.asc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression cursorFilter(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return shorts.id.goe(Long.parseLong(cursor));
    }
}
