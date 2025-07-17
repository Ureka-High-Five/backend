package org.highfive.backend.content.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.aop.AdminOnly;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.request.AdminUpdateContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.dto.response.AdminUpdateContentResponseDto;
import org.highfive.backend.content.service.AdminContentService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @AdminOnly
    @PostMapping
    public Response<AdminAddContentResponseDto> adminAddContent(@Valid @RequestBody final AdminAddContentRequestDto request) {
        return adminContentService.addContent(request);
    }

    @AdminOnly
    @PatchMapping
    public Response<AdminUpdateContentResponseDto> adminUpdateContent(@Valid @RequestBody final AdminUpdateContentRequestDto request) {
        return adminContentService.updateContent(request);
    }

    @AdminOnly
    @DeleteMapping("/{contentId}")
    public Response<Void> adminDeleteContent(@PathVariable Long contentId) {
        return adminContentService.deleteContent(contentId);
    }
}
