package org.highfive.backend.common.fixture;

import java.time.LocalDateTime;
import java.util.List;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.ContentType;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;

public class ContentFixture {

    public static Content createContent(Long contentId) {
        return Content.builder()
                .id(contentId)
                .title("테스트 제목")
                .description("테스트 설명")
                .videoUrl("s3://video.mp4")
                .thumbnailUrl("s3://thumbnail.jpg")
                .postUrl("s3://postUrl.jpg")
                .openDate(LocalDateTime.of(2025, 7, 10, 0, 0))
                .runningTime(120)
                .grade(15)
                .contentType(ContentType.MOVIE)
                .embedding("test-embedding")
                .popularity(100)
                .build();
    }

    public static Content createDefaultContent() {
        return Content.builder()
                .title("테스트 제목")
                .description("테스트 설명")
                .videoUrl("s3://video.mp4")
                .thumbnailUrl("s3://thumbnail.jpg")
                .postUrl("s3://postUrl.jpg")
                .openDate(LocalDateTime.of(2025, 7, 10, 0, 0))
                .runningTime(120)
                .grade(15)
                .contentType(ContentType.MOVIE)
                .embedding("test-embedding")
                .popularity(100)
                .totalRound(10)
                .build();
    }

    public static Content createContentByPopularity(Long contentId, String title, int popularity) {
        return Content.builder()
                .id(contentId)
                .title(title)
                .description("테스트 설명")
                .videoUrl("s3://video.mp4")
                .thumbnailUrl("s3://thumbnail.jpg")
                .postUrl("s3://postUrl.jpg")
                .openDate(LocalDateTime.of(2025, 7, 10, 0, 0))
                .runningTime(120)
                .grade(15)
                .contentType(ContentType.MOVIE)
                .embedding("test-embedding")
                .popularity(popularity)
                .build();
    }

    public static Content createHomeMainContent(Long contentId, String postUrl, String description) {
        return Content.builder()
                .id(contentId)
                .title("title")
                .description(description)
                .videoUrl("s3://video.mp4")
                .thumbnailUrl("s3://thumbnail.jpg")
                .postUrl(postUrl)
                .openDate(LocalDateTime.of(2025, 7, 10, 0, 0))
                .runningTime(120)
                .grade(15)
                .contentType(ContentType.MOVIE)
                .embedding("test-embedding")
                .popularity(100)
                .build();
    }

    public static Content createContentWithMetaInfo(Content content, List<MetaInfoContents> metaInfoContents) {
        return Content.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .videoUrl(content.getVideoUrl())
                .thumbnailUrl(content.getThumbnailUrl())
                .postUrl(content.getPostUrl())
                .openDate(content.getOpenDate())
                .runningTime(content.getRunningTime())
                .grade(content.getGrade())
                .contentType(content.getContentType())
                .embedding(content.getEmbedding())
                .popularity(content.getPopularity())
                .metaInfoContents(metaInfoContents)
                .build();
    }
}
