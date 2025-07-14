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
import org.highfive.backend.content.repository.querydsl.QueryDslMetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final ContentRepository contentRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;
    private final FastApiClient fastApiClient;
    private final QueryDslMetaInfoRepository queryDslMetaInfoRepository;

    @Transactional
    public Response<AdminAddContentResponseDto> addContent(AdminAddContentRequestDto request) {
        Content content = ContentMapper.fromAdminAddContentRequestDto(request);
        String vector = getEmbedding(request);
        content.updateEmbedding(vector);

        Content savedContent = contentRepository.save(content);

        setActors(request, content);
        setDirector(request, content);
        setCountry(request, content);

        return new Response<>(SuccessCode.OK.getCode(), new AdminAddContentResponseDto(savedContent.getId()),null);
    }

    private String getEmbedding(AdminAddContentRequestDto request) {
        List<String> genres = request.getGenres();
        return fastApiClient.calcVectorByGenres(genres);
    }

    private void setActors(AdminAddContentRequestDto request, Content content) {
        List<String> actors = request.getActors();
        for (String actorName : actors) {
            MetaInfo actor = queryDslMetaInfoRepository.findByNameAndType(actorName, MetaType.ACTOR);
            if (actor == null) {
                throw new BusinessException(MetaInfoErrorCode.ACTOR_NOT_FOUND);
            }
            metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(actor).build());
        }
    }

    private void setDirector(AdminAddContentRequestDto request, Content content) {
        String directorName = request.getDirector();
        MetaInfo director = queryDslMetaInfoRepository.findByNameAndType(directorName, MetaType.DIRECTOR);
        if (director == null) {
            throw new BusinessException(MetaInfoErrorCode.DIRECTOR_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(director).build());
    }

    private void setCountry(AdminAddContentRequestDto request, Content content) {
        String countryName = request.getCountryName();
        MetaInfo metaInfo = queryDslMetaInfoRepository.findByNameAndType(countryName, MetaType.COUNTRY);
        if (metaInfo == null) {
            throw new BusinessException(MetaInfoErrorCode.COUNTRY_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(metaInfo).build());
    }
}
