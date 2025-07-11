package org.highfive.backend.common.fixture;

import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaType;

public class MetaInfoFixture {

    public static MetaInfo createMetaInfo(String name, MetaType type) {
        return MetaInfo.builder()
                .name(name)
                .type(type)
                .build();
    }

}
