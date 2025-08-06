package org.highfive.backend.content.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.ActionLogStamp;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.ContentVideoResponseDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.content.service.HomeContentService;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final HomeContentService homeContentService;

    @ActionLogStamp(Action.CLICK)
    @GetMapping("/{contentId}/detail")
    public Response<ContentDetailResponseDto> getContentDetail(
            @PathVariable final Long contentId) {
        return contentService.getContentDetail(contentId);
    }

    @GetMapping("/search")
    public Response<CursorPageResponse<SearchContentResponseDto>> getContentSearch(
            @RequestParam("input") @NotBlank final String input,
            @RequestParam(value = "cursor", required = false) @Nullable final String cursor,
            @RequestParam(value = "size", defaultValue = "10") final int size
    ) {
        return contentService.search(input, cursor, size);
    }

    @GetMapping("/{contentId}/video")
    public Response<ContentVideoResponseDto> getContentVideo(
            @PathVariable final Long contentId
    ) {
        return contentService.getContentVideo(contentId);
    }

    @GetMapping("/vector")
    public Response<List<PersonalRecommendDto>> getVectorContent(
            @AuthenticationPrincipal User user
    ) {
        return Response.ok(homeContentService.recommendContentsByUser(user, 5));
    }
}
