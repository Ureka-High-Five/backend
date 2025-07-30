package org.highfive.backend.infra.s3.dto;

public record UrlPair(String preSignedUrl, String accessUrl) {
}
