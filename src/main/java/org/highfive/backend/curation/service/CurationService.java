package org.highfive.backend.curation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.curation.dto.mapper.CurationMapper;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.entity.Curation;
import org.highfive.backend.curation.entity.CurationContents;
import org.highfive.backend.curation.repository.CurationRepository;
import org.highfive.backend.global.code.SuccessCode;
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

    @Transactional
    public Response<Void> create(final User user, final CreateCurationRequestDto dto) {

        final List<Content> contents = getContentsOrThrow(dto.contents());
        final Curation curation = CurationMapper.toEntity(user, dto);

        addCurationContents(curation, contents);

        curationRepository.save(curation);
        return new Response<>(SuccessCode.OK.getCode(), null, SuccessCode.OK.getMessage());
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
