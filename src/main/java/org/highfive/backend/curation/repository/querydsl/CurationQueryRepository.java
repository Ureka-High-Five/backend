package org.highfive.backend.curation.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.QContent;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.curation.entity.QCuration;
import org.highfive.backend.curation.entity.QCurationContents;
import org.highfive.backend.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CurationQueryRepository {

    private final QCuration curation = QCuration.curation;

    private final JPAQueryFactory queryFactory;

    public Optional<Curation> findCurationWithAll(final Long id) {

        QCurationContents curationContents = QCurationContents.curationContents;
        QContent content = QContent.content;
        QUser user = QUser.user;

        final Curation result = queryFactory
                .selectFrom(curation)
                .distinct()
                .join(curation.user, user).fetchJoin()
                .leftJoin(curation.curationContents, curationContents).fetchJoin()
                .leftJoin(curationContents.content, content).fetchJoin()
                .where(curation.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public List<Curation> findCurationWithUserId(final Long userId, final String cursor, final int size) {

        return queryFactory
                .selectFrom(curation)
                .where(
                        curation.user.id.eq(userId),
                        gtCursor(cursor)
                )
                .orderBy(curation.id.desc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression gtCursor(final String cursor) {
        if(cursor == null) return null;
        return curation.id.lt(Long.parseLong(cursor));
    }
}
