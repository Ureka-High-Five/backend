package org.highfive.backend.content.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.service.AdminContentService;
import org.highfive.backend.global.dto.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @PostMapping("/content")
    public Response<AdminAddContentResponseDto> adminAddContent(@Valid @RequestBody AdminAddContentRequestDto request) {
        return adminContentService.addContent(request);
    }
}
