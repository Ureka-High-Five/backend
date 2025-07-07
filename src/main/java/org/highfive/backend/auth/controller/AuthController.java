package org.highfive.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.controller.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.service.AuthService;
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
    public void login(@RequestBody final OAuthRequestDto OAuthRequestDto) {
        authService.login(OAuthRequestDto);
    }
}
