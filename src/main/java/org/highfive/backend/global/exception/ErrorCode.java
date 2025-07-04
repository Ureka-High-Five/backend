package org.highfive.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("SERVER_001", "서버 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("SEVER_002", "올바르지 않은 입력값입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final String code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
