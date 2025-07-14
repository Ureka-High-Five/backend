package org.highfive.backend.content.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.response.AdminAddContentResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final ContentRepository contentRepository;

    public Response<AdminAddContentResponseDto> addContent(AdminAddContentRequestDto request) {
        Content content = ContentMapper.fromAdminAddContentRequestDto(request);
        Content savedContent = contentRepository.save(content);
        return new Response<>(SuccessCode.OK.getCode(), new AdminAddContentResponseDto(savedContent.getId()),null);
    }
}
