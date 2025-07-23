package org.highfive.backend.infra.s3.service;

import com.nimbusds.common.contenttype.ContentType;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final int DURATION = 5;
    private final String FOLDDER = "curation_thumbnail";

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Response<URL> generatePresignedUrl() {
        final String key = FOLDDER + "/" + UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(ContentType.IMAGE_JPEG.toString())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(DURATION))
                .build();

        final URL url = s3Presigner.presignPutObject(presignRequest).url();

        return Response.ok(url);
    }
}
