package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.dto.ShortsDto;
import org.highfive.backend.shorts.dto.response.ShortsLikedUserItemDto;
import org.highfive.backend.shorts.dto.response.ShortsResponseDto;
import org.highfive.backend.shorts.entity.Shorts;

import java.util.List;
import java.util.stream.Collectors;

public class ShortsMapper {

    public static List<ShortsLikedUserItemDto> toShortsLikedUserResponseDtos(final List<Shorts> shorts) {
        return shorts.stream().map(value -> new ShortsLikedUserItemDto(value.getId(), value.getThumbnailUrl()))
                .collect(Collectors.toList());
    }

    public static ShortsResponseDto toShortsResponseDto(Shorts shorts, boolean liked) {
        return ShortsResponseDto.of(shorts.getId(), shorts.getShortsUrl(), shorts.getContent().getId(),
                shorts.getContent().getTitle(), liked);
    }

    public static ShortsResponseDto toShortsResponseDto(final ShortsDto shorts, final boolean liked) {
        return ShortsResponseDto.of(shorts.id(), shorts.shortsUrl(), shorts.contentId(),
                shorts.title(), liked);
    }

    public static ShortsDto toShortsDto(final Shorts shorts) {
        return new ShortsDto(shorts.getId(), shorts.getShortsUrl(), shorts.getContent().getId(), shorts.getContent().getTitle());
    }

}
