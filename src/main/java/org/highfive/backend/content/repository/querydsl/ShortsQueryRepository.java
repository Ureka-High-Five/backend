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
        boolean hasNext = findShorts.size() > size;
        String nextCursor = hasNext ? findShorts.getLast().getId().toString() : null;
        List<ShortsItemDto> shortsItemDtos = getShortsItemDto(findShorts);
        return new CursorPageResponse<>(shortsItemDtos, hasNext, nextCursor);
    }

    private List<ShortsItemDto> getShortsItemDto(List<Shorts> findShorts) {
        int resultSize = Math.max(findShorts.size() - 1, 0);   // 마지막 1개 제외
        return findShorts.stream()
                .limit(resultSize)                   // ← 끝에서 하나 자르기
                .map(ShortsMapper::toShortsItemDto)
                .toList();
    }

    private BooleanExpression cursorFilter(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;                       // ← 조건 없이 전체 조회
        }
        return shorts.id.goe(Long.parseLong(cursor));
    }
}
