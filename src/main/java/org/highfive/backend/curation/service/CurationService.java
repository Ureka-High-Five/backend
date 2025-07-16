package org.highfive.backend.curation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.dto.request.CurationUpdateRequestDto;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.highfive.backend.curation.dto.mapper.CurationMapper.*;

@Service
@RequiredArgsConstructor
public class CurationService {

    private final ContentRepository contentRepository;
    private final CurationRepository curationRepository;
    private final CurationQueryRepository curationQueryRepository;

    @Transactional
    public Response<Void> create(final User user, final CreateCurationRequestDto dto) {

        final List<Content> contents = getContentsOrThrow(dto.contents());
        final Curation curation = toEntity(user, dto);

        addCurationContents(curation, contents);

        curationRepository.save(curation);
        return Response.ok(null);
    }

    public Response<CurationDetailResponseDto> getCurationDetail(final Long curationId) {
        final Curation curation = curationQueryRepository.findCurationWithAll(curationId)
                .orElseThrow(() -> new BusinessException(CurationErrorCode.CURATION_NOT_FOUND));
        final List<CurationDetailResponseDto.ContentDto> contentDtos = mapToContentDtos(curation.getCurationContents());
        final CurationDetailResponseDto responseDto = toCurationDetailResponseDto(curation, contentDtos);

        return Response.ok(responseDto);
    }

    public Response<CursorPageResponse<MyCurationResponseDto>> getMyCurations(final User user, final String cursor, final int size) {
        final List<Curation> curations = curationQueryRepository.findCurationByUserId(user.getId(), cursor, size);
        final boolean hasNext = curations.size() > size;
        final List<Curation> items = hasNext ? curations.subList(0, size) : curations;
        final String nextCursor = hasNext ? getNextCursor(items) : null;
        final List<MyCurationResponseDto> myCurationResponseDtos = toMyCurationResponseDtos(items);

        final CursorPageResponse<MyCurationResponseDto> response = new CursorPageResponse<>(myCurationResponseDtos,  hasNext, nextCursor);
        return Response.ok(response);
    }

    @Transactional
    public Response<Void> updateCuration(final User user, final Long curationId, final CurationUpdateRequestDto dto) {

        final Curation curation = curationRepository.findById(curationId)
                .orElseThrow(() -> new BusinessException(CurationErrorCode.CURATION_NOT_FOUND));

        checkCurationOwner(curation, user);
        updateTitle(curation, dto.title());
        updateThumbnailUrl(curation, dto.thumbnailUrl());
        updateContents(curation, dto.contents());

        return Response.ok(null);
    }

    @Transactional
    public Response<Void> deleteCuration(final Long curationId, final User user) {
        final Curation curation = curationRepository.findById(curationId).orElseThrow(() -> new BusinessException(CurationErrorCode.CURATION_NOT_FOUND));
        checkCurationOwner(curation, user);
        curationRepository.delete(curation);
        return Response.ok(null);
    }

    private void checkCurationOwner(final Curation curation, final User user) {
        if(!curation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(CurationErrorCode.CURATION_ACCESS_DENIED);
        }
    }

    private void updateTitle(final Curation curation, final String newTitle) {
        if (newTitle != null && !newTitle.equals(curation.getTitle())) {
            curation.updateTitle(newTitle);
        }
    }

    private void updateThumbnailUrl(final Curation curation, final String newThumbnailUrl) {
        if (newThumbnailUrl != null && !newThumbnailUrl.equals(curation.getThumbnailUrl())) {
            curation.updateThumbnailUrl(newThumbnailUrl);
        }
    }

    private void updateContents(final Curation curation, final List<Long> newContentIds) {
        if (newContentIds == null) return;
        if (!isContentUpdated(curation, newContentIds)) return;
        List<Content> newContents = getValidContents(newContentIds);
        replaceCurationContents(curation, newContents);
    }

    private List<Content> getValidContents(final List<Long> contentIds) {
        List<Content> contents = contentRepository.findAllById(contentIds);

        Set<Long> foundIds = contents.stream()
                .map(Content::getId)
                .collect(Collectors.toSet());

        contentIds.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND);
                });

        return contents;
    }

    private boolean isContentUpdated(final Curation curation, final List<Long> newContentIds) {
        Set<Long> originalContentIds = curation.getCurationContents().stream()
                .map(cc -> cc.getContent().getId())
                .collect(Collectors.toSet());

        Set<Long> updatedContentIds = new HashSet<>(newContentIds);

        return !originalContentIds.equals(updatedContentIds);
    }

    private void replaceCurationContents(final Curation curation, final List<Content> newContents) {
        curation.getCurationContents().clear();

        newContents.forEach(content -> {
            final CurationContents cc = CurationContents.builder()
                    .content(content)
                    .build();
            curation.addCurationContents(cc);
        });
    }

    private String getNextCursor(final List<Curation> curations) {
        return String.valueOf(curations.get(curations.size() - 1).getId());
    }

    private List<Content> getContentsOrThrow(final List<Long> contentIds) {
        final List<Content> contents = contentRepository.findAllById(contentIds);
        if(contents.size() != contentIds.size()) {
            throw new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND);
        }
        return contents;
    }

    private void addCurationContents(final Curation curation, final List<Content> contents) {
        contents.forEach(content -> {
            final CurationContents curationContents = CurationContents.builder()
                    .content(content)
                    .build();
            curation.addCurationContents(curationContents);
        });
    }

}
