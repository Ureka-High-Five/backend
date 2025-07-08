package org.highfive.backend.content.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.QContent;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.entity.metadata.QMetaInfo;
import org.highfive.backend.content.entity.metadata.QMetaInfoContents;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int INIT_CONTENT_CNT = 6;

    private final JPAQueryFactory queryFactory;
    
    public List<OnboardingInitContentsResponseDto> getDistinctGenreTopContents() {
        QContent content = QContent.content;
        QMetaInfoContents mic = QMetaInfoContents.metaInfoContents;
        QMetaInfo meta = QMetaInfo.metaInfo;

        List<Tuple> joined = getTuplesByGenreOrderByPopularityDesc(content, meta, mic);

        // 장르 이름 기준으로 서로 다른 장르만 INIT_CONTENT_CNT 개 선택
        return selectContentDistinctGenre(joined, content, meta);
    }

    /**
     * 장르 중복없이 컨텐츠를 선택합니다.
     *
     * @param joined
     * @param content
     * @param meta
     * @return
     */
    private static List<OnboardingInitContentsResponseDto> selectContentDistinctGenre(List<Tuple> joined,
                                                                                                 QContent content,
                                                                                                 QMetaInfo meta) {
        Set<String> initGenres = new HashSet<>();
        List<OnboardingInitContentsResponseDto> finalResult = new ArrayList<>();

        for (Tuple tuple : joined) {
            Content c = tuple.get(content);
            String genreName = tuple.get(meta.name);

            if (initGenres.add(genreName)) {
                finalResult.add(OnboardingInitContentsResponseDto.of(c));
            }

            if (finalResult.size() == INIT_CONTENT_CNT) break;
        }

        return finalResult;
    }

    /**
     * 컨텐츠를 popularity 기준으로 내림차순 정렬하여 컨텐츠와 장르 이름을 반환합니다.
     *
     * @param content
     * @param meta
     * @param mic
     * @return
     */
    private List<Tuple> getTuplesByGenreOrderByPopularityDesc(QContent content, QMetaInfo meta, QMetaInfoContents mic) {
        return queryFactory
                .select(content, meta.name)
                .from(content)
                .join(mic).on(mic.content.id.eq(content.id))
                .join(meta).on(meta.id.eq(mic.metaInfo.id))
                .where(meta.type.eq(MetaType.GENRE))
                .orderBy(content.popularity.desc())
                .fetch();
    }
}
