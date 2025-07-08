package org.highfive.backend.content.repository;

import org.highfive.backend.content.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.global.dto.CursorPageResponse;

public interface ReviewQueryRepository {

    public CursorPageResponse<ReviewSimpleResponseDto> findReviewsByCursor(Long contentId, String cursor, int size);
}
