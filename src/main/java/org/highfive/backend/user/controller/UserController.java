package org.highfive.backend.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.service.UserService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/userInfo")
    public Response<TokenResponseDto> submitOnboarding(@Valid @RequestBody final SubmitOnboardingRequestDto request) {
        return userService.initUser(request);
    }
}
