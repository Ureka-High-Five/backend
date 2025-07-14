package org.highfive.backend.content.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.ContentDetailResponseDto;
import org.highfive.backend.content.dto.response.ContentVideoResponseDto;
import org.highfive.backend.content.dto.response.SearchContentResponseDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

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
    ){
        return contentService.getContentVideo(contentId);
    }

}
