package org.highfive.backend.content.exception;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ShortsErrorCode implements ErrorCode {


    SHORTS_COMMENT_CONTAINS_PROHIBITED_WORD(42202, "Shorts 댓글에 부적절한 단어가 포함되어있습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    SHORTS_NOT_FOUND(40410, "존재하지 않는 Shorts입니다.", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

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
