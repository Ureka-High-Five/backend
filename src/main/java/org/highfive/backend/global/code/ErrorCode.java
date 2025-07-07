package org.highfive.backend.global.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("SERVER_001", "서버 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("SEVER_002", "올바르지 않은 입력값입니다.", HttpStatus.BAD_REQUEST),
    FAST_API_ERROR("40002", "FastAPI 서버에 접근할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    KAKAO_TOKEN_ERROR("OAUTH_001", "카카오 인증 토큰 요청 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    KAKAO_USERINFO_ERROR("OAUTH_002", "카카오 유저 정보 요청 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final String code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}