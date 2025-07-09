package org.highfive.backend.content.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.content.dto.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.content.service.OnboardingService;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OnboardingController {

    private final ContentService contentService;
    private final OnboardingService onboardingService;

    /**
     * 온보딩 초기 6개의 작품을 띄웁니다.
     *
     * @return
     */
    @GetMapping("/content/init")
    public Response<List<OnboardingInitContentsResponseDto>> onboardingInitContents() {
        return new Response<>(SuccessCode.OK.getCode(), contentService.getDistinctGenreTopContents(), null);
    }

    @PostMapping("/content/recommend")
    public Response<List<OnboardingSelectContentResponseDto>> selectContent(@RequestBody final OnboardingSelectContentRequestDto request) {
        final List<OnboardingSelectContentResponseDto> contents = onboardingService.getContentBySelectedContent(request);

        if (contents.isEmpty()) {
            return new Response<>(SuccessCode.NO_CONTENT.getCode(), contents, null);
        }
        return new Response<>(SuccessCode.OK.getCode(), contents, null);
    }
}
