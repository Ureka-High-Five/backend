package org.highfive.backend.content.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.ContentGenreDto;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.QContent;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.metadata.QMetaInfo;
import org.highfive.backend.content.entity.metadata.QMetaInfoContents;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslContentRepository implements ContentQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OnboardingContentDto> findContentsByGenres(List<String> genres, long genreCount) {
        QContent c = QContent.content;
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;

        return queryFactory
                .select(Projections.constructor(OnboardingContentDto.class,
                        c.id,
                        c.postUrl,
                        c.title,
                        c.openDate))
                .from(c)
                .join(mic).on(mic.content.id.eq(c.id))
                .join(m).on(mic.metaInfo.id.eq(m.id))
                .where(
                        m.type.eq(MetaType.GENRE),
                        m.name.in(genres)
                )
                .groupBy(c.id, c.postUrl, c.title, c.openDate)
                .having(m.name.countDistinct().eq(genreCount))
                .fetch();
    }

    public List<GenreCountDto> findTopGenresByContentIds(List<Long> contentIds) {
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;

        return queryFactory
                .select(Projections.constructor(GenreCountDto.class,
                        m.name.as("genre"),
                        mic.count().as("cnt")
                ))
                .from(mic)
                .join(m).on(mic.metaInfo.id.eq(m.id))
                .where(
                        m.type.eq(MetaType.GENRE),
                        mic.content.id.in(contentIds)
                )
                .groupBy(m.name)
                .orderBy(mic.count().desc())
                .limit(2)
                .fetch();
    }

    public List<OnboardingContentDto> findContentsByGenresOrderByMatchCountDesc(List<String> genres) {
        QContent c = QContent.content;
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;

        return queryFactory
                .select(Projections.constructor(OnboardingContentDto.class,
                        c.id,
                        c.postUrl,
                        c.title,
                        c.openDate,
                        m.name.countDistinct()
                ))
                .from(c)
                .join(mic).on(mic.content.id.eq(c.id))
                .join(m).on(mic.metaInfo.id.eq(m.id))
                .where(
                        m.type.eq(MetaType.GENRE),
                        m.name.in(genres)
                )
                .groupBy(c.id, c.postUrl, c.title, c.openDate)
                .orderBy(m.name.countDistinct().desc())
                .fetch();
    }@SuppressWarnings("unchecked")

    public List<Map<String, Object>> findContentGenresByContentIds(List<Long> contentIds) {

        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;
        QContent c = QContent.content;

        // 1) 먼저 DTO 리스트로 조회
        List<ContentGenreDto> dtoList = queryFactory
                .select(Projections.fields(
                        ContentGenreDto.class,
                        c.id.as("contentId"),
                        m.name.as("genreName")
                ))
                .from(mic)
                .join(mic.metaInfo, m)
                .join(mic.content, c)
                .where(
                        m.type.eq(MetaType.GENRE),
                        c.id.in(contentIds)
                )
                .fetch();

        // 2) DTO → Map 변환 후 반환
        return dtoList.stream()
                .map(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("contentId", dto.getContentId());
                    map.put("genreName", dto.getGenreName());
                    return map;
                })
                .toList();
    }

    @Override
    public Optional<Content> findWithMetaInfoById(Long contentId){
        QContent content = QContent.content;
        QMetaInfoContents metaInfoContents = QMetaInfoContents.metaInfoContents;
        QMetaInfo metaInfo = QMetaInfo.metaInfo;

        Content result = queryFactory.selectFrom(content)
                .leftJoin(content.metaInfoContents,metaInfoContents).fetchJoin()
                .leftJoin(metaInfoContents.metaInfo, metaInfo).fetchJoin()
                .where(content.id.eq(contentId))
                .distinct()
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
