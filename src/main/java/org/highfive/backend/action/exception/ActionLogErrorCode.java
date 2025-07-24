package org.highfive.backend.action.exception;

import lombok.Getter;
import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public enum ActionLogErrorCode implements ErrorCode {

    ACTION_LOG_NOT_FOUND(40413, "존재하지 않는 행동 로그입니다.", HttpStatus.NOT_FOUND),
    ACTION_LOG_NOT_SAVE(50001, "행동 로그 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ActionLogErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
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
        return this.httpStatus;
    }
}
