package org.highfive.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.aop.AdminOnly;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.request.UpdateUserRoleRequestDto;
import org.highfive.backend.user.dto.response.GetAllUserResponseDto;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.service.AdminUserService;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @AdminOnly
    @GetMapping("/user")
    public Response<CursorPageResponse<GetAllUserResponseDto>> getAllUser(
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return adminUserService.getAllUser(cursor, size);
    }

    @AdminOnly
    @GetMapping("/user/role")
    public Response<Void> updateUserRole(
            @RequestBody UpdateUserRoleRequestDto request
    ){
        return adminUserService.updateUserRole(request);
    }
}
