package org.highfive.backend.curation.dto.mapper;

import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.user.entity.User;

public class CurationMapper {

    public static Curation toEntity(final User user, final CreateCurationRequestDto dto) {
        return Curation.builder()
                .user(user)
                .title(dto.title())
                .description(dto.description())
                .thumbnailUrl(dto.thumbnail())
                .build();
    }
}
