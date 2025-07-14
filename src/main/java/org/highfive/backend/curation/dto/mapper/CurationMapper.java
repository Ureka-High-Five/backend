package org.highfive.backend.curation.dto.mapper;

import org.highfive.backend.content.dto.response.HomeContentsResponseDto;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.dto.response.CurationDetailResponseDto;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.user.entity.User;

import java.util.List;

public class CurationMapper {

    public static Curation toEntity(final User user, final CreateCurationRequestDto dto) {
        return Curation.builder()
                .user(user)
                .title(dto.title())
                .description(dto.description())
                .thumbnailUrl(dto.thumbnail())
                .build();
    }

    public static CurationDetailResponseDto toCurationDetailResponseDto(final Curation curation, final List<CurationDetailResponseDto.ContentDto> contentDtos) {
        return new CurationDetailResponseDto(
                curation.getTitle(),
                contentDtos,
                curation.getThumbnailUrl(),
                curation.getUser().getProfileUrl(),
                curation.getUser().getName(),
                curation.getUser().getId()
        );
    }
}
