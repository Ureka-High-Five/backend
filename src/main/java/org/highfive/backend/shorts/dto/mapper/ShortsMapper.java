package org.highfive.backend.shorts.dto.mapper;

import org.highfive.backend.shorts.dto.response.ShortsItemDto;
import org.highfive.backend.shorts.entity.Shorts;

public class ShortsMapper {

    public static ShortsItemDto toShortsItemDto(Shorts shorts) {
        return new ShortsItemDto(shorts.getContent().getId(), shorts.getContent().getTitle(), shorts.getId() ,shorts.getShortsUrl());
    }
}
