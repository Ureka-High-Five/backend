package org.highfive.backend.content.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("{contentId}/detail")
    public Response<ContentDetailResponseDto> getContentDetail(
            @PathVariable final Long contentId) {
        return contentService.getContentDetail(contentId);
    }

    @GetMapping("/recommend")
    public Response<HomeContentsResponseDto> homeContents(@AuthenticationPrincipal final User user) {
        MainRecommendDto mainRecommend = contentService.recommendMainContentsByUser(user);
        List<PersonalRecommendDto> personalRecommends = contentService.recommendContentsByUser(user, 4);
        Map<String, List<GenreContentDto>> genreRecommends = contentService.recommendContentsByUserGenre(user, 2);

        // todo 사용자가 선호하는 장르 기반 큐레이션 조회(1차 MVP 이후)

        HomeContentsResponseDto result = new HomeContentsResponseDto(mainRecommend, personalRecommends, genreRecommends, null);
        return new Response<>(SuccessCode.OK.getCode(), result, null);
    }

    @GetMapping("/search")
    public Response<CursorPageResponse<SearchContentResponseDto>> getContentSearch(
            @RequestParam("input") @NotBlank final String input,
            @RequestParam(value = "cursor", required = false) @Nullable final String cursor,
            @RequestParam(value = "size", defaultValue = "10") final int size
    ) {
        return contentService.search(input, cursor, size);
    }

}
