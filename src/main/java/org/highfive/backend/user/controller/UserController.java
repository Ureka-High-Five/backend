package org.highfive.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.response.RatedContentResponseDto;
import org.highfive.backend.user.dto.response.UserInfoResponseDto;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Response<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal final User user) {
        return userService.getMyInfo(user);
    }

    @GetMapping("/reviews")
    public Response<CursorPageResponse<RatedContentResponseDto>> getMyReviews(@AuthenticationPrincipal final User user,
                                                                              @RequestParam(required = false) final String cursor,
                                                                              @RequestParam(defaultValue = "3") final int size) {
        return userService.getMyReviews(user, cursor, size);
    }
}
