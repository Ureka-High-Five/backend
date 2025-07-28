package org.highfive.backend.common.fixture;

import java.util.List;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.shorts.entity.Shorts;

public class ShortsFixture {

    public static Shorts createShorts(Content content) {
        return Shorts.builder()
                .content(content)
                .shortsUrl("test-url")
                .thumbnailUrl("test-url")
                .shortsLikeTimeLogs(List.of())
                .likeCount(10)
                .trailerTime(100)
                .build();
    }

    public static Shorts createShortsById(Content content, Long id) {
        return Shorts.builder()
                .id(id)
                .content(content)
                .shortsUrl("test-url")
                .thumbnailUrl("test-url")
                .shortsLikeTimeLogs(List.of())
                .likeCount(10)
                .trailerTime(100)
                .build();
    }
}
