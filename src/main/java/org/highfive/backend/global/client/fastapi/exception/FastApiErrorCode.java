package org.highfive.backend.global.client.fastapi.exception;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FastApiErrorCode implements ErrorCode {

    FAST_API_ERROR(50001, "FastAPI 서버에 접근할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FAST_API_RESPONSE_NULL(50201, "FastAPI 응답 바디가 null입니다.", HttpStatus.BAD_GATEWAY),
    FAST_API_RESPONSE_ERROR(50202, "FastAPI 서버가 에러 응답을 반환했습니다.", HttpStatus.BAD_GATEWAY),
    FAST_API_CONNECTION_ERROR(50401, "FastAPI 서버에 연결할 수 없습니다.", HttpStatus.GATEWAY_TIMEOUT);

    private final int code;
    private final String message;
    private final HttpStatus status;

    FastApiErrorCode(final int code, final String message, final HttpStatus status) {
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
