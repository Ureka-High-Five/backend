package org.highfive.backend.curation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.curation.dto.mapper.CurationMapper;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.dto.response.CurationDetailResponseDto;
import org.highfive.backend.curation.dto.response.MyCurationResponseDto;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.curation.entity.CurationContents;
import org.highfive.backend.curation.exception.CurationErrorCode;
import org.highfive.backend.curation.repository.jpa.CurationRepository;
import org.highfive.backend.curation.repository.querydsl.CurationQueryRepository;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
@RequestMapping("/curation")
public class CurationService {

    private final ContentRepository contentRepository;
    private final CurationRepository curationRepository;
    private final CurationQueryRepository curationQueryRepository;

    @Transactional
    public Response<Void> create(final User user, final CreateCurationRequestDto dto) {

        final List<Content> contents = getContentsOrThrow(dto.contents());
        final Curation curation = CurationMapper.toEntity(user, dto);

        addCurationContents(curation, contents);

        curationRepository.save(curation);
        return Response.ok(null);
    }

    public Response<CurationDetailResponseDto> getCurationDetail(final Long curationId) {
        final Curation curation = curationQueryRepository.findCurationWithAll(curationId)
                .orElseThrow(() -> new BusinessException(CurationErrorCode.CURATION_NOT_FOUND));
        final List<CurationDetailResponseDto.ContentDto> contentDtos = mapToContentDtos(curation.getCurationContents());
        final CurationDetailResponseDto responseDto = CurationMapper.toCurationDetailResponseDto(curation, contentDtos);

        return Response.ok(responseDto);
    }

    public Response<CursorPageResponse<MyCurationResponseDto>> getMyCurations(final User user, final String cursor, final int size) {
        List<Curation> curations = curationQueryRepository.findCurationWithUserId(user.getId(), cursor, size);
        final boolean hasNext = curations.size() > size;
        curations = hasNext ? curations.subList(0, size) : curations;

        List<MyCurationResponseDto> myCurationResponseDtos = curations.stream()
                .map(curation -> new MyCurationResponseDto(curation.getId(), curation.getTitle(), curation.getThumbnailUrl()))
                .toList();

        final String nextCursor = hasNext ? String.valueOf(curations.get(curations.size() - 1).getId()) : null;
        CursorPageResponse<MyCurationResponseDto> response = new CursorPageResponse<>(myCurationResponseDtos, nextCursor);
        return Response.ok(response);

    }

    private List<CurationDetailResponseDto.ContentDto> mapToContentDtos(final List<CurationContents> curationContentsList) {
        return curationContentsList.stream()
                .map(curationContent -> {
                    final Content content = curationContent.getContent();
                    return new CurationDetailResponseDto.ContentDto(
                            content.getId(),
                            content.getTitle(),
                            content.getThumbnailUrl()
                    );
                })
                .toList();
    }

    private List<Content> getContentsOrThrow(final List<Long> contentIds) {
        final List<Content> contents = contentRepository.findAllById(contentIds);
        if(contents.size() != contentIds.size()) {
            throw new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND);
        }
        return contents;
    }

    private void addCurationContents(final Curation curation, final List<Content> contents) {
        for(Content content : contents) {
            final CurationContents curationContents = CurationContents.builder()
                    .content(content)
                    .build();
            curation.addCurationContents(curationContents);
        }
    }

}
