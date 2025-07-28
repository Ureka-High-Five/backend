package org.highfive.backend.infra.s3.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.infra.s3.MediaType;
import org.highfive.backend.infra.s3.dto.PresignedUploadResponse;
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

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Response<PresignedUploadResponse> generatePresignedUrl(final String type) {

        final MediaType mediaType = MediaType.from(type);
        final String key = mediaType.getFolder() + "/" + UUID.randomUUID();
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(mediaType.getContentType())
                .build();

        final PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(DURATION))
                .build();

        final URL url = s3Presigner.presignPutObject(presignRequest).url();

        return Response.ok(new PresignedUploadResponse(url.toString(), getImageUrl(key)));
    }

    private String getImageUrl(final String key) {
        return "https://" + bucket + ".s3." + awsRegion + ".amazonaws.com/" + key;
    }
}
