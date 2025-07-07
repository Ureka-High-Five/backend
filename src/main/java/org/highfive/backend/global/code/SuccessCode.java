package org.highfive.backend.global.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    OK(20000, "요청이 정상 처리되었습니다.", HttpStatus.OK),
    USER_CREATED(20100, "회원가입이 완료되었습니다.", HttpStatus.CREATED),;

    private final int code;
    private final String message;
    private final HttpStatus status;

    SuccessCode(final int code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
