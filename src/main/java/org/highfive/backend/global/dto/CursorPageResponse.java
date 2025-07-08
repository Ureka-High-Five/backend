package org.highfive.backend.global.dto;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> items,
        String nextCursor
) {
}
