package org.highfive.backend.shorts.dto.response;

public record ShortsAndLikedItemDto (
        Long contentId,
        String contentTitle,
        Long shortsId,
        String shortsUrl,
        boolean liked
) {

}
