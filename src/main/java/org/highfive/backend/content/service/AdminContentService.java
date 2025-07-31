package org.highfive.backend.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.request.AdminUpdateContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.dto.response.AdminUpdateContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiVectorFromGenresDto;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.infra.s3.MediaType;
import org.highfive.backend.infra.s3.service.S3Service;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaInfoContents;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.metadata.exception.MetaInfoErrorCode;
import org.highfive.backend.metadata.repository.jpa.MetaInfoContentsRepository;
import org.highfive.backend.metadata.repository.jpa.MetaInfoRepository;
import org.highfive.backend.shorts.entity.Shorts;
import org.highfive.backend.shorts.repository.jpa.ShortsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final ContentRepository contentRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;
    private final FastApiClient fastApiClient;
    private final MetaInfoRepository metaInfoRepository;
    private final S3Service s3Service;
    private final ShortsRepository shortsRepository;

    @Transactional
    public Response<AdminAddContentResponseDto> addContent(final AdminAddContentRequestDto request) {
        final Content content = ContentMapper.fromAdminAddContentRequestDto(request);
        final String vector = getEmbeddingByGenres(request.genres());
        final String uuid = request.uuid();
        content.updateEmbedding(vector);

        final Content savedContent = contentRepository.save(content);
        updatePosterThumbnailUrl(savedContent, uuid);

        setGenres(request, content);
        setActors(request, content);
        setDirector(request, content);
        setCountry(request, content);
        updateShorts(content, uuid, request.trailerTime());

        return Response.ok(new AdminAddContentResponseDto(savedContent.getId()));
    }

    private void updatePosterThumbnailUrl(final Content content, final String uuid) {
        final String posterThumbnailUrl = s3Service.createThumbnailUrl(MediaType.POSTER_THUMBNAIL_URL, uuid);
        content.updateThumbnailUrl(posterThumbnailUrl);
    }

    private void updateShorts(final Content content, final String uuid, final int trailerTime) {
        final String shortsThumbnailUrl = s3Service.createThumbnailUrl(MediaType.SHORTS_THUMBNAIL_URL, uuid);
        final String shortsSegmentUrl = s3Service.createSegmentUrl(MediaType.SHORTS_SEGMENT, uuid);
        final Shorts shorts = Shorts.builder()
                .shortsUrl(shortsSegmentUrl)
                .trailerTime(trailerTime)
                .thumbnailUrl(shortsThumbnailUrl)
                .likeCount(0)
                .build();

        shorts.updateContent(content);
        shortsRepository.save(shorts);
    }

    @Transactional
    public Response<AdminUpdateContentResponseDto> updateContent(final AdminUpdateContentRequestDto request) {
        final Content content = getUpdateContent(request);

        updateGenres(request, content);
        updateDirector(request, content);
        updateActors(request, content);

        final FastApiVectorFromGenresDto response = fastApiClient.vectorFromGenres(request.genres());
        content.updateEmbedding(response.vector());
        return Response.ok(new AdminUpdateContentResponseDto(content.getId()));
    }

    @Transactional
    public Response<Void> deleteContent(long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
        boolean isDeleted = content.delete();
        if (!isDeleted) {
            throw new BusinessException(ContentErrorCode.CONTENT_ALREADY_DELETED);
        }

        return Response.ok(null);
    }

    private Content getUpdateContent(final AdminUpdateContentRequestDto request) {
        final Content content = contentRepository.findById(request.contentId())
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND));
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
                actor = metaInfoRepository.save(
                        MetaInfo.builder()
                        .type(MetaType.ACTOR)
                        .name(actorName)
                        .build());
            }
            metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(actor).build());
        }
    }

    private void setDirector(final AdminAddContentRequestDto request, final Content content) {
        final String directorName = request.director();
        MetaInfo director = metaInfoRepository.findByNameAndType(directorName, MetaType.DIRECTOR);
        if (director == null) {
            director = metaInfoRepository.save(
                            MetaInfo.builder()
                            .type(MetaType.DIRECTOR)
                            .name(directorName)
                            .build());
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(director).build());
    }

    private void setGenres(AdminAddContentRequestDto request, Content content) {
        final List<String> genres = request.genres();
        for (String genreName : genres) {
            MetaInfo genre = metaInfoRepository.findByNameAndType(genreName, MetaType.GENRE);
            if (genre == null) {
                genre = metaInfoRepository.save(
                        MetaInfo.builder()
                                .type(MetaType.GENRE)
                                .name(genreName)
                                .build());
            }
            metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(genre).build());
        }
    }

    private String getEmbeddingByGenres(final List<String> genres) {
        return fastApiClient.vectorFromGenres(genres).vector();
    }
}
