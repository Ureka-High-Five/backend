package org.highfive.backend.global.client.fastapi.exception;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FastApiErrorCode implements ErrorCode {

    FAST_API_ERROR(40002, "FastAPI 서버에 접근할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
