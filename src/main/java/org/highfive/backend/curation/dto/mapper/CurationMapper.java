package org.highfive.backend.curation.dto.mapper;

import java.util.List;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.CurationContentsDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.CurationDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.dto.response.CurationDetailResponseDto;
import org.highfive.backend.curation.dto.response.MyCurationResponseDto;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.curation.entity.CurationContents;
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

    public static CurationDetailResponseDto toCurationDetailResponseDto(final Curation curation,
                                                                        final List<CurationDetailResponseDto.ContentDto> contentDtos) {
        return new CurationDetailResponseDto(
                curation.getTitle(),
                contentDtos,
                curation.getThumbnailUrl(),
                curation.getUser().getProfileUrl(),
                curation.getUser().getName(),
                curation.getDescription(),
                curation.getUser().getId()
        );
    }

    public static List<MyCurationResponseDto> toMyCurationResponseDtos(final List<Curation> curations) {
        return curations.stream()
                .map(curation -> new MyCurationResponseDto(
                        curation.getId(),
                        curation.getTitle(),
                        curation.getThumbnailUrl(),
                        curation.getDescription()
                ))
                .toList();
    }

    public static List<CurationDetailResponseDto.ContentDto> mapToContentDtos(
            final List<CurationContents> curationContents) {
        return curationContents.stream()
                .map(curationContent -> {
                    final Content content = curationContent.getContent();
                    return new CurationDetailResponseDto.ContentDto(
                            content.getId(),
                            content.getTitle(),
                            content.getThumbnailUrl(),
                            content.getDescription()
                    );
                })
                .toList();
    }

    public static List<CurationDto> toCurationDto(List<Curation> curations) {
        return curations.stream().map(c -> {
                    List<CurationContents> curationContents = c.getCurationContents();
                    List<CurationContentsDto> curationContentsDtoList = curationContents.stream()
                            .map(cc -> new CurationContentsDto(cc.getContent().getId(), cc.getContent().getThumbnailUrl()))
                            .toList();
                    return new CurationDto(
                            c.getId(),
                            c.getUser().getId(),
                            c.getUser().getName(),
                            curationContentsDtoList,
                            c.getTitle(),
                            c.getUser().getProfileUrl());
                    }
                )
                .limit(4)
                .toList();
    }
}
