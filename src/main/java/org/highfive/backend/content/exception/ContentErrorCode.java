package org.highfive.backend.content.exception;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.code.ErrorCode;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ContentErrorCode implements ErrorCode {

    CONTENT_NOT_FOUND(40402, "존재하지 않는 컨텐츠입니다.", HttpStatus.NOT_FOUND),
    CONTENT_ALREADY_DELETED(40001, "이미 삭제된 컨텐츠입니다.", HttpStatus.BAD_REQUEST),
    CONTENT_ACCESS_DENIED(40303, "컨텐츠에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    VIDEO_TYPE_NOT_FOUND(40411, "존재하지 않는 Video Type입니다.", HttpStatus.NOT_FOUND),
    ID_CASTING_ERROR(40003, "컨텐츠 아이디는 Long으로 입력해주세요.", HttpStatus.BAD_REQUEST),
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
