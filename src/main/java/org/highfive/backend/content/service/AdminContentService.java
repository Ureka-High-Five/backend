package org.highfive.backend.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.exception.MetaInfoErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.repository.jpa.MetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.code.GlobalErrorCode;
import org.highfive.backend.global.code.SuccessCode;
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
        if (!user.isAdmin()) {
            throw new BusinessException(GlobalErrorCode.ACCESS_DENIED);
        }

        final Content content = ContentMapper.fromAdminAddContentRequestDto(request);
        final String vector = getEmbedding(request);
        content.updateEmbedding(vector);

        final Content savedContent = contentRepository.save(content);

        setActors(request, content);
        setDirector(request, content);
        setCountry(request, content);

        return new Response<>(SuccessCode.OK.getCode(), new AdminAddContentResponseDto(savedContent.getId()),null);
    }

    private String getEmbedding(final AdminAddContentRequestDto request) {
        List<String> genres = request.genres();
        return fastApiClient.vectorFromGenres(genres).vector();
    }

    private void setActors(final AdminAddContentRequestDto request, final Content content) {
        List<String> actors = request.actors();
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

    private void setCountry(AdminAddContentRequestDto request, Content content) {
        final String countryName = request.countryName();
        final MetaInfo metaInfo = metaInfoRepository.findByNameAndType(countryName, MetaType.COUNTRY);
        if (metaInfo == null) {
            throw new BusinessException(MetaInfoErrorCode.COUNTRY_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(metaInfo).build());
    }
}
