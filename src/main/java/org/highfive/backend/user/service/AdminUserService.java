package org.highfive.backend.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.response.GetAllUserResponseDto;
import org.highfive.backend.user.dto.response.SearchUserResponseDto;
import org.highfive.backend.user.repository.querydsl.UserQueryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserQueryRepository userQueryRepository;

    public Response<CursorPageResponse<GetAllUserResponseDto>> getAllUser(final Long cursor, final Integer size) {
        return Response.ok(userQueryRepository.findByCursor(cursor, size));
    }

    public Response<List<SearchUserResponseDto>> searchUser(String username) {
        return null;
    }
}
