package org.highfive.backend.content.dto.mapper;

import org.highfive.backend.content.dto.response.ShortsItemDto;
import org.highfive.backend.content.entity.shorts.Shorts;

public class ShortsMapper {

    public static ShortsItemDto toShortsItemDto(Shorts shorts) {
        return new ShortsItemDto(shorts.getId(), shorts.getContent().getId().toString(), shorts.getId() ,shorts.getShortsUrl());
    }
}
