package org.highfive.backend.global.client.fastapi.dto.request;

import java.util.List;

public record FastApiVectorByGenresRequestDto(
        List<String> genres
) {
}
