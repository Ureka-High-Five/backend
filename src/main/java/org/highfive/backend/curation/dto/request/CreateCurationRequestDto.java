package org.highfive.backend.curation.dto.request;

import java.util.List;

public record CreateCurationRequestDto(
    String title,
    List<Long> contents,
    String thumbnail
) {
}