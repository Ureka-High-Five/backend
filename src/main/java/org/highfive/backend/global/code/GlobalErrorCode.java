package org.highfive.backend.global.code;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(50000, "서버 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(40000, "잘못된 요청 입니다.", HttpStatus.BAD_REQUEST),
    JSON_PARSING_ERROR(42201, "JSON 파싱에 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    ACCESS_DENIED(40300, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    ;

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