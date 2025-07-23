package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.dto.response.GetShortsCommentResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsCommentsByIdResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsCommentsByTimeResponseDto;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.entity.ShortsComment;
import org.highfive.backend.user.entity.User;

public class ShortsCommentMapper {
    public static ShortsComment toShortsComment(CreateShortsCommentRequestDto requestDto, User user, Shorts shorts) {
        return ShortsComment.of(user, shorts, requestDto.comment(), requestDto.time());
    }

    public static GetShortsCommentResponseDto toGetShortsCommentResponseDto(ShortsComment shortsComment) {
        User user = shortsComment.getUser();
        return new GetShortsCommentResponseDto(
                shortsComment.getId(), shortsComment.getMessage(), user.getName(), user.getProfileUrl(), user.getId());
    }

    public static ShortsCommentsByTimeResponseDto toShortsCommentsByTimeResponseDto(ShortsComment sc) {
        return new ShortsCommentsByTimeResponseDto(sc.getTime(), sc.getUser().getName(), sc.getUser().getProfileUrl(),
                sc.getMessage(), sc.getUser().getId());
    }

    public static ShortsCommentsByIdResponseDto toShortsCommentsByIdResponseDto(ShortsComment sc) {
        return new ShortsCommentsByIdResponseDto(sc.getUser().getName(), sc.getUser().getProfileUrl(), sc.getMessage(),
                sc.getUser().getId(), sc.getCreatedAt().toString());
    }
}
