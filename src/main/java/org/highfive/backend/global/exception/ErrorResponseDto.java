package org.highfive.backend.global.exception;

import org.highfive.backend.global.code.ErrorCode;

public record ErrorResponseDto(String code, String message) {
    public ErrorResponseDto(final ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
