package org.highfive.backend.infra.s3;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MediaType {
    CURATION_IMAGE("image/jpeg", "curation_thumbnail",".jpg"),
    CONTENT_IMAGE("image/jpeg", "poster", ".jpg"),
    VIDEO("video/mp4", "video",".mp4"),
    SHORTS("video/mp4", "shorts_video",".mp4"),
    VIDEO_SEGMENT("video/mp4", "video_segment", ".m3u8"),
    SHORTS_SEGMENT("video/mp4", "shorts_segment", ".m3u8"),
    POSTER_THUMBNAIL_URL("image/jpeg", "poster_thumbnail", ".jpg"),
    SHORTS_THUMBNAIL_URL("image/jpeg", "shorts_thumbnail", ".jpg");

    private final String contentType;
    private final String folder;
    private final String extension;

    MediaType(String contentType, String folder, String extension) {
        this.contentType = contentType;
        this.folder = folder;
        this.extension = extension;
    }

    public static MediaType from(final String type) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 미디어 타입입니다: " + type));
    }
}
