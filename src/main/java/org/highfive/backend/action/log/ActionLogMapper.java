package org.highfive.backend.action.log;

import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;

import java.util.ArrayList;
import java.util.List;

public class ActionLogMapper {

    public static MetaInfoLog extractMetaInfoLog(final List<MetaInfoContents> metaINfoContents) {
        List<String> genres = new ArrayList<>();
        List<String> actors = new ArrayList<>();
        String director = "";
        String country = "";

        for(MetaInfoContents metaInfoContents : metaINfoContents) {
            MetaInfo metaInfo = metaInfoContents.getMetaInfo();
            switch(metaInfo.getType()) {
                case ACTOR -> actors.add(metaInfo.getName());
                case GENRE -> genres.add(metaInfo.getName());
                case DIRECTOR -> director = metaInfo.getName();
                case COUNTRY -> country = metaInfo.getName();
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
