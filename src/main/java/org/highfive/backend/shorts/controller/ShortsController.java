package org.highfive.backend.shorts.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.action.Action;
import org.highfive.backend.action.ActionLogStamp;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsDislikeRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeCreateRequestDto;
import org.highfive.backend.shorts.dto.response.CreateShortsCommentResponseDto;
import org.highfive.backend.shorts.dto.response.GetShortsCommentResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsCommentsByIdResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsCommentsByTimeResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsLikeTimeResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsLikedUserItemDto;
import org.highfive.backend.shorts.dto.response.ShortsResponseDto;
import org.highfive.backend.shorts.service.ShortsService;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shorts")
public class ShortsController {

    private final ShortsService shortsService;

    @PostMapping("/comment")
    public Response<CreateShortsCommentResponseDto> createShortsComment(
            @Valid @RequestBody final CreateShortsCommentRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return shortsService.createShortsComment(requestDto, user);
    }

    @GetMapping("/{shortsId}")
    public Response<ShortsResponseDto> getShortsById(@PathVariable Long shortsId, @AuthenticationPrincipal User user) {
        return shortsService.getShortsById(shortsId, user);
    }

    @GetMapping
    public Response<CursorPageResponse<ShortsResponseDto>> recommendShorts(
            @RequestParam(required = false) final Long cursor,
            @RequestParam(defaultValue = "5", required = false) @Positive final Integer size,
            @AuthenticationPrincipal User user
    ) {
        return shortsService.recommendShorts(cursor, size, user);
    }

    @GetMapping("/{shortsId}/comments")
    public Response<List<ShortsCommentsByTimeResponseDto>> getCommentsByTime(
            @PathVariable Long shortsId,
            @RequestParam @Positive Long time,
            @RequestParam @Min(5) Integer duration) {
        return shortsService.getCommentsByTime(shortsId, time, duration);
    }

    @PostMapping("/like")
    @ActionLogStamp(Action.LIKE)
    public Response<Void> like(@AuthenticationPrincipal final User user,
                               @RequestBody @Valid final ShortsLikeCreateRequestDto dto) {
        return shortsService.like(user, dto);
    }

    @PostMapping("/dislike")
    @ActionLogStamp(Action.DISLIKE)
    public Response<Void> dislike(@AuthenticationPrincipal final User user,
                                  @RequestBody @Valid final ShortsDislikeRequestDto dto) {
        return shortsService.dislike(user, dto);
    }

    @GetMapping("/like")
    public Response<ShortsLikeTimeResponseDto> getShortsLike(@RequestParam long shortsId,
                                                             @RequestParam @Positive int duration,
                                                             @AuthenticationPrincipal User user) {
        return shortsService.getShortsLike(shortsId, duration, user);
    }

    @GetMapping("/comment")
    public Response<GetShortsCommentResponseDto> getOneShortsComment(
            @RequestParam final Long shortsId,
            @RequestParam final Long time
    ) {
        return shortsService.getOneShortsComment(shortsId, time);
    }

    @GetMapping("/liked")
    public Response<CursorPageResponse<ShortsLikedUserItemDto>> likedShorts(final @AuthenticationPrincipal User user,
                                                                            @RequestParam @Nullable final String cursor,
                                                                            @RequestParam(defaultValue = "5") final int size) {
        return shortsService.likedShorts(user, cursor, size);
    }

    @GetMapping("/{shortsId}/comments/id")
    public Response<CursorPageResponse<ShortsCommentsByIdResponseDto>> commentsById(
            @PathVariable Long shortsId,
            @RequestParam @Positive Long cursor,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        return shortsService.commentsByIdAndCursor(shortsId, cursor, size);
    }

    @GetMapping("/content/{contentId}")
    public Response<ShortsResponseDto> getShortsByContent(
            @PathVariable Long contentId,
            @AuthenticationPrincipal User user
    ) {
        return shortsService.getShortsByContent(contentId, user);
    }
}
