package org.highfive.backend.user.service;

import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.mapper.UserMapper;
import org.highfive.backend.user.dto.response.UserInfoResponseDto;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public Response<UserInfoResponseDto> getMyInfo(final User user) {
        return Response.ok(UserMapper.from(user));
    }
}
