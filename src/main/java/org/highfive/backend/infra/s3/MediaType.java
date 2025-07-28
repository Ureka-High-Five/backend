package org.highfive.backend.infra.s3;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MediaType {
    IMAGE("image/jpeg", "curation_thumbnail"),
    VIDEO("video/mp4", "video");

    private final String contentType;
    private final String folder;

    MediaType(String contentType, String folder) {
        this.contentType = contentType;
        this.folder = folder;
    }

    public static MediaType from(final String type) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 미디어 타입입니다: " + type));
    }
}
