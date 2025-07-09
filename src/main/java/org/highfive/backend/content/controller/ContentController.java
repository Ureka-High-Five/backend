package org.highfive.backend.content.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.home.HomeContentsResponseDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.home.RecommendContentDto;
import org.highfive.backend.content.dto.response.home.RecommendGenreContentDto;
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

    public Response<HomeContentsResponseDto> homeContents(@AuthenticationPrincipal final User user) {
        List<RecommendContentDto> contentsByUser = contentService.getContentsByUser(user);
        RecommendGenreContentDto contentsByTopGenre = contentService.getContentsByUserGenre(user, 1);

        // todo 사용자가 가장 선호하는 장르 기반 큐레이션 조회(1차 MVP 이후)

        RecommendGenreContentDto contentsBySecondGenre = contentService.getContentsByUserGenre(user, 2);

        // todo 사용자가 두번째로 선호하는 장르 기반 큐레이션 조회(1차 MVP 이후)

        RecommendContentDto randomContent = contentService.getRandomContent();

        HomeContentsResponseDto result = new HomeContentsResponseDto(contentsByUser, contentsByTopGenre, contentsBySecondGenre, null, null, randomContent);
        return new Response<>(SuccessCode.OK.getCode(), result, null);
    }
}
