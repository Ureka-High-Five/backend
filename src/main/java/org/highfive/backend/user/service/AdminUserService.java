package org.highfive.backend.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.mapper.UserMapper;
import org.highfive.backend.user.dto.response.GetAllUserResponseDto;
import org.highfive.backend.user.dto.response.SearchUserResponseDto;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.highfive.backend.user.repository.querydsl.UserQueryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;

    public Response<CursorPageResponse<GetAllUserResponseDto>> getAllUser(final Long cursor, final Integer size) {
        return Response.ok(userQueryRepository.findByCursor(cursor, size));
    }

    public Response<CursorPageResponse<SearchUserResponseDto>> searchUser(String username, Long cursor, int size) {
        return Response.ok(userQueryRepository.findByNameContaining(username, cursor, size));
    }
}
