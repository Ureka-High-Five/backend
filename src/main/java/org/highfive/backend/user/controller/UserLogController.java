package org.highfive.backend.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.user.dto.request.CreateContentWatchLogRequestDto;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.service.UserLogService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserLogController {

    private final UserLogService userLogService;

    @PostMapping("/content/watch-log")
    public Response<Void> createContentWatchLog(
            @Valid @RequestBody final CreateContentWatchLogRequestDto request,
            @AuthenticationPrincipal User user
    ){
        return userLogService.createContentWatchLog(request, user);
    }
}
