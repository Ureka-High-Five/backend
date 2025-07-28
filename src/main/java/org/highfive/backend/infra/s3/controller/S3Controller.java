package org.highfive.backend.infra.s3.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.aop.EditorOnly;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.infra.s3.dto.PresignedUploadResponse;
import org.highfive.backend.infra.s3.service.S3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @EditorOnly
    @GetMapping("/presignedUrl")
    public Response<PresignedUploadResponse> getPresignedUrl(@PathVariable final String type) {
        return s3Service.generatePresignedUrl(type);
    }
}
