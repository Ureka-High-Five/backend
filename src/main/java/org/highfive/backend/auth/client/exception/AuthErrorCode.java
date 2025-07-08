package org.highfive.backend.auth.client.exception;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {

    KAKAO_TOKEN_ERROR(40101, "카카오 인증 토큰 요청 오류입니다.", HttpStatus.BAD_GATEWAY),
    KAKAO_USERINFO_ERROR(40102, "카카오 유저 정보 요청 오류입니다.", HttpStatus.BAD_GATEWAY);

    private final int code;
    private final String message;
    private final HttpStatus status;

    AuthErrorCode(final int code, final String message, final HttpStatus status) {
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
