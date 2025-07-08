package org.highfive.backend.content.exception;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MetaInfoErrorCode implements ErrorCode {
    GENRE_NOT_FOUND(40404, "존재하지 않는 장르입니다.", HttpStatus.NOT_FOUND)
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }
}
