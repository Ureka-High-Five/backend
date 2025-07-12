package org.highfive.backend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.dto.request.ReissueRequestDto;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.service.AuthService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Response<?> login(@RequestBody @Valid final OAuthRequestDto OAuthRequestDto) {
        return authService.login(OAuthRequestDto);
    }

    @PostMapping("/reissue")
    public Response<TokenResponseDto> reissueToken(@RequestBody @Valid final ReissueRequestDto reissueRequestDto) {
        return authService.reissue(reissueRequestDto);
    }

    @PostMapping("/logout")
    public Response<Void> logout(@AuthenticationPrincipal final User user, final HttpServletRequest request) {
        return authService.logout(user, request);
    }
}