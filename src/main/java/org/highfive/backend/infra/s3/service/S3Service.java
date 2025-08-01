package org.highfive.backend.infra.s3.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.infra.s3.MediaType;
import org.highfive.backend.infra.s3.dto.ContentsPresignedUrlResponseDto;
import org.highfive.backend.infra.s3.dto.CurationPresignedUrlResponseDto;
import org.highfive.backend.infra.s3.dto.UrlPair;
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

    private static final int DURATION_MINUTES = 5;

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Response<ContentsPresignedUrlResponseDto> generateContentsPreSignedUrl() {
        final UUID uuid = UUID.randomUUID();

        final UrlPair image = generatePreSignedPair(MediaType.CONTENT_IMAGE, uuid);
        final UrlPair shorts = generatePreSignedPair(MediaType.SHORTS, uuid);
        final UrlPair video = generatePreSignedPair(MediaType.VIDEO, uuid);

        return Response.ok(new ContentsPresignedUrlResponseDto(
                image.preSignedUrl(), image.accessUrl(),
                shorts.preSignedUrl(), shorts.accessUrl(),
                video.preSignedUrl(), video.accessUrl(),
                uuid.toString()
        ));
    }

    public Response<CurationPresignedUrlResponseDto> generateCurationPreSignedUrl() {
        final UUID uuid = UUID.randomUUID();

        final UrlPair image = generatePreSignedPair(MediaType.CURATION_IMAGE, uuid);

        return Response.ok(new CurationPresignedUrlResponseDto(
                image.preSignedUrl(), image.accessUrl()
        ));
    }

    private UrlPair generatePreSignedPair(MediaType mediaType, UUID uuid) {
        String key = "";
        if(mediaType.equals(MediaType.CONTENT_IMAGE) || mediaType.equals(MediaType.CURATION_IMAGE)) {
            key = buildImageKey(mediaType, uuid);
        }

        if(mediaType.equals(MediaType.SHORTS) || mediaType.equals(MediaType.VIDEO)) {
            key = buildVideoKey(mediaType, uuid);
        }

        final String preSignedUrl = createPreSignedUrl(key, mediaType.getContentType());
        final String accessUrl = buildUrl(key);
        return new UrlPair(preSignedUrl, accessUrl);
    }

    private String createPreSignedUrl(String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(DURATION_MINUTES))
                .build();

        URL url = s3Presigner.presignPutObject(presignRequest).url();
        return url.toString();
    }

    public String createThumbnailUrl(final MediaType mediaType, final String uuid) {
        final String key = buildImageKey(mediaType, UUID.fromString(uuid));
        return buildUrl(key);
    }

    public String createSegmentUrl(final MediaType mediaType, final String uuid) {
        final String key = buildVideoKey(mediaType, UUID.fromString(uuid));
        return buildUrl(key);
    }

    private String buildImageKey(final MediaType mediaType, final UUID uuid) {
        return mediaType.getFolder() + "/" + uuid + mediaType.getExtension();
    }

    private String buildVideoKey(final MediaType mediaType, final UUID uuid) {
        return mediaType.getFolder() + "/" + uuid + "/" + uuid + mediaType.getExtension();
    }

    private String buildUrl(final String key) {
        return "https://" + bucket + ".s3." + awsRegion + ".amazonaws.com/" + key;
    }
}