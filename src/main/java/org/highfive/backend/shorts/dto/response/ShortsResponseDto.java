package org.highfive.backend.shorts.dto.response;

import org.highfive.backend.content.dto.VideoType;

public record ShortsResponseDto(
        Long shortsId,
        String shortsUrl,
        Long contentId,
        String contentTitle,
        boolean liked,
        VideoType videoType
) {
    public static ShortsResponseDto of(Long shortsId, String shortsUrl, Long contentId, String contentTitle, boolean liked) {
        return new ShortsResponseDto(shortsId, shortsUrl, contentId, contentTitle, liked, VideoType.SHORTS);
    }
}
