package org.highfive.backend.curation.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.QContent;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.curation.entity.QCuration;
import org.highfive.backend.curation.entity.QCurationContents;
import org.highfive.backend.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CurationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Curation> findCurationWithAll(final Long id) {

        QCuration curation = QCuration.curation;
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
}
