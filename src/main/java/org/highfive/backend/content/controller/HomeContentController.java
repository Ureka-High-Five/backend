package org.highfive.backend.content.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.content.service.HomeContentService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeContentController {

    private final HomeContentService homeContentService;

    @GetMapping("/content/home")
    public Response<HomeContentsResponseDto> homeContents(@AuthenticationPrincipal final User user) {
        return homeContentService.getHomeContents(user);
    }
}
