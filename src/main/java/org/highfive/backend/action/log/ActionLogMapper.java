package org.highfive.backend.action.log;

import java.util.HashMap;
import java.util.Map;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;

import java.util.ArrayList;
import java.util.List;

public class ActionLogMapper {

    public static MetaInfoLog extractMetaInfoLog(final List<MetaInfoContents> metaINfoContents) {
        Map<Long, String> genres = new HashMap<>();
        Map<Long, String> actors = new HashMap<>();
        Map<Long, String> director = new HashMap<>();
        Map<Long, String> country = new HashMap<>();

        for(MetaInfoContents metaInfoContents : metaINfoContents) {
            MetaInfo metaInfo = metaInfoContents.getMetaInfo();
            switch(metaInfo.getType()) {
                case ACTOR -> actors.put(metaInfo.getId(), metaInfo.getName());
                case GENRE -> genres.put(metaInfo.getId(), metaInfo.getName());
                case DIRECTOR -> director.put(metaInfo.getId(), metaInfo.getName());
                case COUNTRY -> country.put(metaInfo.getId(), metaInfo.getName());
            }
        }

        return MetaInfoLog.builder()
                .genres(genres)
                .actors(actors)
                .country(country)
                .director(director)
                .build();
    }
}
