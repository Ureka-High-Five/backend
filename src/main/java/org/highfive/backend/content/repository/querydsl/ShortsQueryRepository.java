package org.highfive.backend.content.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ShortsMapper;
import org.highfive.backend.content.dto.response.ShortsItemDto;
import org.highfive.backend.content.entity.shorts.QShorts;
import org.highfive.backend.content.entity.shorts.Shorts;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShortsQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QShorts shorts = QShorts.shorts;

    public CursorPageResponse<ShortsItemDto> findByCursor(final String cursor, final int size) {
        List<Shorts> findShorts = jpaQueryFactory
                .select(shorts)
                .from(shorts)
                .where(
                        cursorFilter(cursor)
                ).orderBy(shorts.id.asc())
                .limit(size + 1)
                .fetch();
        final boolean hasNext = findShorts.size() > size;
        final String nextCursor = hasNext ? findShorts.getLast().getId().toString() : null;
        final List<ShortsItemDto> shortsItemDtos = getShortsItemDto(findShorts);
        return new CursorPageResponse<>(shortsItemDtos, hasNext, nextCursor);
    }

    private List<ShortsItemDto> getShortsItemDto(List<Shorts> findShorts) {
        final int resultSize = Math.max(findShorts.size() - 1, 0);
        return findShorts.stream()
                .limit(resultSize)
                .map(ShortsMapper::toShortsItemDto)
                .toList();
    }

    private BooleanExpression cursorFilter(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return shorts.id.goe(Long.parseLong(cursor));
    }
}
