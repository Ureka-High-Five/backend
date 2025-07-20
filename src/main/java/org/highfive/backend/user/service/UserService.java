package org.highfive.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.review.repository.jpa.ReviewRepository;
import org.highfive.backend.user.dto.mapper.UserMapper;
import org.highfive.backend.user.dto.response.RatedContentResponseDto;
import org.highfive.backend.user.dto.response.UserInfoResponseDto;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ReviewRepository reviewRepository;

    public Response<UserInfoResponseDto> getMyInfo(final User user) {
        return Response.ok(UserMapper.from(user));
    }

    public Response<CursorPageResponse<RatedContentResponseDto>> getMyReviews(final User user, final String cursor, final int size) {
        final List<RatedContentResponseDto> ratedContentResponseDtos = reviewRepository.findByUser(user.getId(), cursor, size + 1);

        boolean hasNext = ratedContentResponseDtos.size() > size;
        String nextCursor = null;

        if (hasNext) {
            RatedContentResponseDto last = ratedContentResponseDtos.remove(size);
            nextCursor = String.valueOf(last.id());
        }

        return Response.ok(new CursorPageResponse<>(ratedContentResponseDtos, hasNext, nextCursor));
    }
}
