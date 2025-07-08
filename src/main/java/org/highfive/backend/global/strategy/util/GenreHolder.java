package org.highfive.backend.global.strategy.util;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.metadata.QMetaInfo;
import org.highfive.backend.content.entity.metadata.QMetaInfoContents;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenreHolder {

    private static final Set<String> genres = new HashSet<>();
    private static final Map<String, Double> weightMap = new HashMap<>();

    private final JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        QMetaInfo metaInfo = QMetaInfo.metaInfo;
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;

        List<String> genreList = getGenreList(metaInfo, mic);

        genres.clear();
        weightMap.clear();
        for (String genre : genreList) {
            genres.add(genre);
            weightMap.put(genre, 0.0);
        }
    }

    private List<String> getGenreList(QMetaInfo metaInfo, QMetaInfoContents mic) {
        return queryFactory
                .select(metaInfo.name)
                .from(mic)
                .join(metaInfo).on(mic.metaInfo.id.eq(metaInfo.id))
                .where(metaInfo.type.eq(MetaType.GENRE))
                .distinct()
                .fetch();
    }

    public static Map<String, Double> toWeightMap() {
        return new HashMap<>(weightMap);
    }

    public static Set<String> getGenres() {
        return new HashSet<>(genres);
    }
}
