package org.highfive.backend.curation.exception;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CurationErrorCode implements ErrorCode {
    CURATION_NOT_FOUND(40409, null, HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    CurationErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

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
