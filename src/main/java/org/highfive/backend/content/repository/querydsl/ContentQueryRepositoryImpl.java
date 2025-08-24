package org.highfive.backend.content.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.ContentDetailDto;
import org.highfive.backend.content.dto.ContentGenreDto;
import org.highfive.backend.content.dto.MetaInfoDto;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.dto.response.TopContentsByGenreDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.QContent;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.metadata.entity.QMetaInfo;
import org.highfive.backend.metadata.entity.QMetaInfoContents;
import org.highfive.backend.shorts.entity.QShorts;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentQueryRepositoryImpl implements ContentQueryRepository {

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

    public List<OnboardingContentDto> findContentsByGenresOrderByMatchCountDesc(List<String> genres, List<Long> recommendedContentIds) {
        QContent c = QContent.content;
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;

        return queryFactory
                .select(Projections.constructor(OnboardingContentDto.class,
                        c.id,
                        c.thumbnailUrl,
                        c.title,
                        c.openDate,
                        m.name.countDistinct()
                ))
                .from(c)
                .join(mic).on(mic.content.id.eq(c.id))
                .join(m).on(mic.metaInfo.id.eq(m.id))
                .where(
                        m.type.eq(MetaType.GENRE),
                        m.name.in(genres),
                        c.id.notIn(recommendedContentIds)
                )
                .groupBy(c.id, c.thumbnailUrl, c.title, c.openDate)
                .orderBy(m.name.countDistinct().desc())
                .fetch();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findContentGenresByContentIds(List<Long> contentIds) {

        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;
        QContent c = QContent.content;

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

        return dtoList.stream()
                .map(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("contentId", dto.getContentId());
                    map.put("genreName", dto.getGenreName());
                    return map;
                })
                .toList();
    }

    public Optional<ContentDetailDto> findContentDetailById(final long contentId) {
        QContent c = QContent.content;
        QShorts s = QShorts.shorts;

        final SubQueryExpression<Long> latestShortId = JPAExpressions
                .select(s.id.max())
                .from(s)
                .where(s.content.id.eq(c.id));

        ContentDetailDto dto = queryFactory
                .select(Projections.constructor(
                        ContentDetailDto.class,
                        c.title,
                        c.runningTime,
                        c.grade,
                        c.postUrl,
                        c.openDate,
                        c.description,
                        latestShortId,
                        c.videoUrl
                ))
                .from(c)
                .where(c.id.eq(contentId))
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    public Optional<MetaInfoDto> findMetaInfoById(final long contentId) {
        QMetaInfoContents metaInfoContents = QMetaInfoContents.metaInfoContents;
        QMetaInfo metaInfo = QMetaInfo.metaInfo;

        final List<Tuple> rows = queryFactory
                .select(metaInfo.type, metaInfo.name)
                .from(metaInfoContents)
                .join(metaInfoContents.metaInfo, metaInfo)
                .where(metaInfoContents.content.id.eq(contentId))
                .fetch();

        final Map<MetaType, List<String>> typeValue = rows.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(metaInfo.type),
                        Collectors.mapping(t -> t.get(metaInfo.name), Collectors.toList())
                ));

        final List<String> actors = typeValue.getOrDefault(MetaType.ACTOR, List.of());
        final List<String> genres = typeValue.getOrDefault(MetaType.GENRE, List.of());
        final String director = typeValue.getOrDefault(MetaType.DIRECTOR, List.of())
                .stream().findFirst().orElse(null);

        return Optional.of(new MetaInfoDto(genres, actors, director));
    }

    @Override
    public Optional<Content> findWithMetaInfoById(final Long contentId) {
        QContent content = QContent.content;
        QMetaInfoContents metaInfoContents = QMetaInfoContents.metaInfoContents;
        QMetaInfo metaInfo = QMetaInfo.metaInfo;

        Content result = queryFactory
                .selectFrom(content)
                .leftJoin(content.metaInfoContents, metaInfoContents).fetchJoin()
                .leftJoin(metaInfoContents.metaInfo, metaInfo).fetchJoin()
                .where(content.id.eq(contentId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public List<TopContentsByGenreDto> findTopContentsByGenreRandom(String genre, int limit) {
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo m = QMetaInfo.metaInfo;
        QContent c = QContent.content;

        return queryFactory
                .select(Projections.constructor(TopContentsByGenreDto.class, c.id, c.thumbnailUrl))
                .from(mic)
                .join(mic.metaInfo, m)
                .join(mic.content, c)
                .where(
                        m.type.eq(MetaType.valueOf("GENRE")),
                        m.name.eq(genre),
                        c.deletedAt.isNull()
                )
                .orderBy(Expressions.numberTemplate(Double.class, "function('RANDOM')").asc())
                .limit(limit)
                .fetch();
    }
}
