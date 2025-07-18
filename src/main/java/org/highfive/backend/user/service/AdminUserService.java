package org.highfive.backend.user.service;

import static org.highfive.backend.user.exception.UserErrorCode.ADMIN_CHANGE_FORBIDDEN;
import static org.highfive.backend.user.exception.UserErrorCode.USER_NOT_FOUND_ERROR;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.dto.request.UpdateUserRoleRequestDto;
import org.highfive.backend.user.dto.response.GetAllUserResponseDto;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.repository.UserRepository;
import org.highfive.backend.user.repository.querydsl.UserQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;

    public Response<CursorPageResponse<GetAllUserResponseDto>> getAllUser(final Long cursor, final Integer size) {
        return Response.ok(userQueryRepository.findByCursor(cursor, size));
    }


    @Transactional
    public Response<Void> updateUserRole(final UpdateUserRoleRequestDto request) {

        if(Objects.equals(request.role(), UserRole.ADMIN)){
            throw new BusinessException(ADMIN_CHANGE_FORBIDDEN);
        }

        User existedUser = userRepository.findById(request.userId())
                        .orElseThrow(()-> new BusinessException(USER_NOT_FOUND_ERROR));

        existedUser.updateUserRole(request.role());

        return Response.ok(null);
    }
}
