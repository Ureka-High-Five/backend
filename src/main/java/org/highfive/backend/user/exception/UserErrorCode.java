package org.highfive.backend.user.exception;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND_ERROR(40400, "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    ADMIN_CHANGE_FORBIDDEN(40304, "ADMIN 권한으로의 변경은 불가합니다.", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(final int code, final String message, final HttpStatus status) {
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

