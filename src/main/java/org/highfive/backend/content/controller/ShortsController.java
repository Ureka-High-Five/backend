package org.highfive.backend.content.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.content.service.ShortsService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shorts")
public class ShortsController {

    private final ShortsService shortsService;

    @PostMapping("/comment")
    public Response<Void> createShortsComment(
            @Valid @RequestBody final CreateShortsCommentRequestDto requestDto,
            @AuthenticationPrincipal User user
    ){
        return shortsService.createShortsComment(requestDto, user);
    }





}
