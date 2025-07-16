package org.highfive.backend.content.service;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.request.AdminUpdateContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.dto.response.AdminUpdateContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.metadata.exception.MetaInfoErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.metadata.repository.jpa.MetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiVectorFromGenresDto;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final ContentRepository contentRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;
    private final FastApiClient fastApiClient;
    private final MetaInfoRepository metaInfoRepository;

    @Transactional
    public Response<AdminAddContentResponseDto> addContent(final AdminAddContentRequestDto request, final User user) {
        validateAdmin(user);

        final Content content = ContentMapper.fromAdminAddContentRequestDto(request);
        final String vector = getEmbeddingByGenres(request.genres());
        content.updateEmbedding(vector);

        final Content savedContent = contentRepository.save(content);

        setActors(request, content);
        setDirector(request, content);
        setCountry(request, content);

        return Response.ok(new AdminAddContentResponseDto(savedContent.getId()));
    }

    @Transactional
    public Response<AdminUpdateContentResponseDto> updateContent(@Valid final AdminUpdateContentRequestDto request, final User user) {
        validateAdmin(user);
        final Content content = updateContent(request);

        updateGenres(request, content);
        updateDirector(request, content);
        updateActors(request, content);

        final FastApiVectorFromGenresDto response = fastApiClient.vectorFromGenres(request.genres());
        content.updateEmbedding(response.vector());
        return Response.ok(new AdminUpdateContentResponseDto(content.getId()));
    }

    private Content updateContent(final AdminUpdateContentRequestDto request) {
        final Content content = contentRepository.findById(request.contentId()).orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
        content.updateFromDto(request);
        contentRepository.save(content);
        return content;
    }

    private void updateGenres(final AdminUpdateContentRequestDto request, final Content content) {
        final List<String> genres = request.genres();
        metaInfoContentsRepository.deleteAllByContentAndType(content.getId(), MetaType.GENRE);
        for (String genreName : genres) {
            final MetaInfo genre = metaInfoRepository.findByNameAndType(genreName, MetaType.GENRE);
            if (genre == null) {
                throw new BusinessException(MetaInfoErrorCode.GENRE_NOT_FOUND);
            }
            metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(genre).build());
        }
    }

    private void updateDirector(final AdminUpdateContentRequestDto request, final Content content) {
        metaInfoContentsRepository.deleteAllByContentAndType(content.getId(), MetaType.DIRECTOR);
        final MetaInfo director = metaInfoRepository.findByNameAndType(request.director(), MetaType.DIRECTOR);
        if (director == null) {
            throw new BusinessException(MetaInfoErrorCode.DIRECTOR_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(director).build());
    }

    private void updateActors(final AdminUpdateContentRequestDto request, final Content content) {
        final List<String> actors = request.actors();
        metaInfoContentsRepository.deleteAllByContentAndType(content.getId(), MetaType.ACTOR);
        for (String actorName : actors) {
            MetaInfo actor = metaInfoRepository.findByNameAndType(actorName, MetaType.ACTOR);
            if (actor == null) {
                throw new BusinessException(MetaInfoErrorCode.ACTOR_NOT_FOUND);
            }
            metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(actor).build());
        }
    }

    private void setCountry(final AdminAddContentRequestDto request, final Content content) {
        final String countryName = request.countryName();
        final MetaInfo metaInfo = metaInfoRepository.findByNameAndType(countryName, MetaType.COUNTRY);
        if (metaInfo == null) {
            throw new BusinessException(MetaInfoErrorCode.COUNTRY_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(metaInfo).build());
    }

    private void setActors(final AdminAddContentRequestDto request, final Content content) {
        final List<String> actors = request.actors();
        for (String actorName : actors) {
            MetaInfo actor = metaInfoRepository.findByNameAndType(actorName, MetaType.ACTOR);
            if (actor == null) {
                throw new BusinessException(MetaInfoErrorCode.ACTOR_NOT_FOUND);
            }
            metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(actor).build());
        }
    }

    private void setDirector(final AdminAddContentRequestDto request, final Content content) {
        final String directorName = request.director();
        final MetaInfo director = metaInfoRepository.findByNameAndType(directorName, MetaType.DIRECTOR);
        if (director == null) {
            throw new BusinessException(MetaInfoErrorCode.DIRECTOR_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(director).build());
    }

    private String getEmbeddingByGenres(final List<String> genres) {
        return fastApiClient.vectorFromGenres(genres).vector();
    }

    private void validateAdmin(final User user) {
        if (!user.isAdmin()) {
            throw new BusinessException(GlobalErrorCode.ACCESS_DENIED);
        }
    }
}
