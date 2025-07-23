package org.highfive.backend.infra.s3.controller;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.aop.EditorOnly;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.infra.s3.service.S3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @EditorOnly
    @GetMapping("/presignedUrl")
    public Response<URL> getPresignedUrl() {
        return s3Service.generatePresignedUrl();
    }
}
