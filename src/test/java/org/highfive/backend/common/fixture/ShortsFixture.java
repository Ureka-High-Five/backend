package org.highfive.backend.common.fixture;

import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.shorts.Shorts;

public class ShortsFixture {

    public static Shorts createShorts(Content content) {
        return Shorts.builder()
                .content(content)
                .shortsUrl("test-url")
                .thumbnailUrl("test-url")
                .shortsLikeTimeLogs(null)
                .build();
    }
}
