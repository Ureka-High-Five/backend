package org.highfive.backend.content.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.content.service.OnboardingService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping("/content/init")
    public Response<List<OnboardingInitContentsResponseDto>> onboardingInitContents() {
        return onboardingService.getDistinctGenreTopContents();
    }

    @PostMapping("/content/recommend")
    public Response<List<OnboardingSelectContentResponseDto>> selectContent(
            @Valid @RequestBody final OnboardingSelectContentRequestDto request) {
        return onboardingService.getContentBySelectedContent(request);
    }

    @PatchMapping("/user/info")
    public Response<TokenResponseDto> submitOnboarding(@Valid @RequestBody final SubmitOnboardingRequestDto request) {
        return onboardingService.initUser(request);
    }
}
