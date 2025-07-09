package org.highfive.backend.content.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    /**
     * 온보딩 초기 6개의 작품을 띄웁니다.
     *
     * @return
     */
    @GetMapping("/init")
    public Response<List<OnboardingInitContentsResponseDto>> onboardingInitContents() {
        return new Response<>(SuccessCode.OK.getCode(), contentService.getDistinctGenreTopContents(), null);
    }

    @GetMapping("{contentId}/detail")
    public Response<ContentDetailResponseDto> getContentDetail(
            @PathVariable final Long contentId,
            @AuthenticationPrincipal final User user) {
        return contentService.getContentDetail(contentId, user);
    }
}
