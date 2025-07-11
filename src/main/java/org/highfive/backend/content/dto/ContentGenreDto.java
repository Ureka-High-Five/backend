package org.highfive.backend.content.dto;

public record ContentGenreDto (
    Long contentId,
    String genreName
) {
    public ContentGenreDto() {
        this(null, null);
    }
}
