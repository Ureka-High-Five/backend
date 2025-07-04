package org.highfive.backend.global.exception;

public record ErrorResponseDto(String code, String message) {
    public ErrorResponseDto(final ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
