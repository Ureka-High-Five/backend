package org.highfive.backend.global.code;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode{

    INTERNAL_SERVER_ERROR(50000, "서버 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(40000, "잘못된 요청 입니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;

    GlobalErrorCode(final int code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.status;
    }
}