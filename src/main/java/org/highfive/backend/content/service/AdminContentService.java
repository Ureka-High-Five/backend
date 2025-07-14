package org.highfive.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ContentMapper;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaInfoContents;
import org.highfive.backend.content.exception.MetaInfoErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.MetaInfoContentsRepository;
import org.highfive.backend.content.repository.MetaInfoRepository;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final ContentRepository contentRepository;
    private final MetaInfoRepository metaInfoRepository;
    private final MetaInfoContentsRepository metaInfoContentsRepository;

    @Transactional
    public Response<AdminAddContentResponseDto> addContent(AdminAddContentRequestDto request) {
        Content content = ContentMapper.fromAdminAddContentRequestDto(request);
        Content savedContent = contentRepository.save(content);
        String countryName = request.getCountryName();
        MetaInfo metaInfo = metaInfoRepository.findByCountryName(countryName);
        if (metaInfo == null) {
            throw new BusinessException(MetaInfoErrorCode.COUNTRY_NOT_FOUND);
        }
        metaInfoContentsRepository.save(MetaInfoContents.builder().content(content).metaInfo(metaInfo).build());
        return new Response<>(SuccessCode.OK.getCode(), new AdminAddContentResponseDto(savedContent.getId()),null);
    }
}
