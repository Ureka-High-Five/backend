package org.highfive.backend.review.exception;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_CONTAINS_PROHIBITED_WORD(42200, "리뷰에 부적절한 단어가 포함되어있습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    REVIEW_NOT_FOUND(40403, "존재하지 않는 리뷰입니다.", HttpStatus.NOT_FOUND),
    REVIEW_FORBIDDEN(40301, "해당 리뷰에 대한 권한이 없는 사용자입니다.", HttpStatus.FORBIDDEN),
    MY_REVIEW_NOT_FOUND(40405, "나의 리뷰가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
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
