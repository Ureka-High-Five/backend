package org.highfive.backend.content.repository.querydsl;

import static org.highfive.backend.content.entity.metadata.QMetaInfo.metaInfo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslMetaInfoRepository {

    private final JPAQueryFactory queryFactory;

    public MetaInfo findByNameAndType(String name, MetaType type) {
        return queryFactory
                .selectFrom(metaInfo)
                .where(metaInfo.name.eq(name), metaInfo.type.eq(type))
                .fetchOne();
    }
}
