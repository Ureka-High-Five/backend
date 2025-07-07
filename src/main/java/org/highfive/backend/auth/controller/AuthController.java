package org.highfive.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.controller.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.service.AuthService;
import org.highfive.backend.global.dto.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public Response<?> login(@RequestBody final OAuthRequestDto OAuthRequestDto) {
        return authService.login(OAuthRequestDto);
    }
}
