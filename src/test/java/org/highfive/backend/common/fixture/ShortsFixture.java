package org.highfive.backend.common.fixture;

import java.util.List;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.shorts.entity.Shorts;

public class ShortsFixture {

    public static Shorts createDefaultShorts() {
        return Shorts.builder()
                .id(1L) // 테스트용 id (DB 자동 생성이므로 실제 테스트에서는 생략 가능)
                .content(null) // 임시 Content
                .shortsUrl("http://example.com/shorts/1")
                .likeCount(0)
                .thumbnailUrl("http://example.com/thumbnail/1.png")
                .trailerTime(15)
                .build();
    }

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
