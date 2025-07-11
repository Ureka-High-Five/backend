package org.highfive.backend.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentGenreDto {
    private Long contentId;
    private String genreName;
}
