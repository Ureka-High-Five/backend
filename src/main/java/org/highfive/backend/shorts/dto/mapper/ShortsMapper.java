package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.dto.response.ShortsResponseDto;
import org.highfive.backend.shorts.dto.response.ShortsItemDto;
import org.highfive.backend.shorts.dto.response.ShortsLikedUserItemDto;
import org.highfive.backend.shorts.dto.response.ShortsResponseDto;
import org.highfive.backend.shorts.entity.Shorts;

import java.util.List;
import java.util.stream.Collectors;

public class ShortsMapper {

    public static ShortsItemDto toShortsItemDto(Shorts shorts) {
        return new ShortsItemDto(shorts.getContent().getId(), shorts.getContent().getTitle(), shorts.getId() ,shorts.getShortsUrl());
    }

    public static List<ShortsLikedUserItemDto> toShortsLikedUserResponseDtos(final List<Shorts> shorts) {
        return shorts.stream().map(value -> new ShortsLikedUserItemDto(value.getId(), value.getThumbnailUrl())).collect(Collectors.toList());
    }

    public static ShortsResponseDto toShortsResponseDto(Shorts shorts) {
        return new ShortsResponseDto(shorts.getId(), shorts.getShortsUrl(), shorts.getContent().getId(), shorts.getContent().getTitle());
    }
}
