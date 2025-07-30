package org.highfive.backend.infra.s3.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.aop.EditorOnly;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.infra.s3.dto.ContentsPresignedUrlResponseDto;
import org.highfive.backend.infra.s3.dto.CurationPresignedUrlResponseDto;
import org.highfive.backend.infra.s3.service.S3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @EditorOnly
    @GetMapping("/presignedUrl/curation")
    public Response<CurationPresignedUrlResponseDto> curationPreSignedUrl() {
        return s3Service.generateCurationPreSignedUrl();
    }

    @EditorOnly
    @GetMapping("/presignedUrl/contents")
    public Response<ContentsPresignedUrlResponseDto> contentsPreSignedUrl() {
        return s3Service.generateContentsPreSignedUrl();
    }
}
