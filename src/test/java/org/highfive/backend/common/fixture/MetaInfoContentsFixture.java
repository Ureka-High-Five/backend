package org.highfive.backend.common.fixture;

import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;

public class MetaInfoContentsFixture {

    public static MetaInfoContents createMetaInfoContents(MetaInfo metaInfo, Content content) {
        return MetaInfoContents.builder()
                .metaInfo(metaInfo)
                .content(content)
                .build();
    }
}
