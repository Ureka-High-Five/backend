package org.highfive.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.controller.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.service.AuthService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public Response<?> login(@RequestBody final OAuthRequestDto OAuthRequestDto) {
        return authService.login(OAuthRequestDto);
    }

    @GetMapping
    public void tokenTest(@AuthenticationPrincipal final User user) {
        System.out.println(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public void adminTest(@AuthenticationPrincipal final User user) {
        System.out.println(user);
        System.out.println("adminTest");
    }
}
