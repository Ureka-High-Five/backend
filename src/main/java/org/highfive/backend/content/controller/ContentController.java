package org.highfive.backend.content.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
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
    private final UserRepository userRepository;

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

    @GetMapping("/recommend")
//    public Response<HomeContentsResponseDto> homeContents(@AuthenticationPrincipal final User user) {
        public Response<HomeContentsResponseDto> homeContents() {
        User user = userRepository.findById(1l).orElseThrow(RuntimeException::new);
        MainRecommendDto mainRecommend = contentService.recommendMainContentsByUser(user);
        List<PersonalRecommendDto> personalRecommends = contentService.recommendContentsByUser(user, 4);
        Map<String, List<GenreContentDto>> genreRecommends = contentService.recommendContentsByUserGenre(user, 2);

        // todo 사용자가 선호하는 장르 기반 큐레이션 조회(1차 MVP 이후)

        HomeContentsResponseDto result = new HomeContentsResponseDto(mainRecommend, personalRecommends, genreRecommends, null);
        return new Response<>(SuccessCode.OK.getCode(), result, null);
    }
}
