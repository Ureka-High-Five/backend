package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.dto.response.GetShortsCommentResponseDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.user.entity.User;

public class ShortsCommentMapper {
    public static ShortsComment toShortsComment(CreateShortsCommentRequestDto requestDto, User user, Shorts shorts){
        return ShortsComment.of(user,shorts, requestDto.comment(), requestDto.time());
    }

    public static GetShortsCommentResponseDto toGetShortsCommentResponseDto(ShortsComment shortsComment){
        User user = shortsComment.getUser();
        return new GetShortsCommentResponseDto(
                shortsComment.getMessage(),user.getName(), user.getProfileUrl(),user.getId());
    }
}
