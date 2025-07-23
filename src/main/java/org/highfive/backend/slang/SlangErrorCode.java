package org.highfive.backend.slang;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SlangErrorCode implements ErrorCode {
    SLANG_INPUT(40004, "금칙어가 포함된 입력입니다.", HttpStatus.BAD_REQUEST),
    ;

    SlangErrorCode(final int code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    private final int code;
    private final String message;
    private final HttpStatus status;

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
