package org.highfive.backend.content.repository;

import org.highfive.backend.content.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.user.entity.User;

public interface ReviewQueryRepository {

    CursorPageResponse<ReviewSimpleResponseDto> findReviewsByCursor(Long contentId, String cursor, int size, User user);
}
