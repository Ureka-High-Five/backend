package org.highfive.backend.common.fixture;

import org.highfive.backend.content.entity.Content;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;

public class MetaInfoContentsFixture {

    public static MetaInfoContents createMetaInfoContents(MetaInfo metaInfo, Content content) {
        return MetaInfoContents.builder()
                .metaInfo(metaInfo)
                .content(content)
                .build();
    }
}
