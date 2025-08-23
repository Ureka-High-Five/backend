package org.highfive.backend.content.dto;

import java.util.List;

public record MetaInfoDto(
        List<String> genres,
        List<String> actors,
        String director
) {
}
