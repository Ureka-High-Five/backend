package org.highfive.backend.content.exception;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MetaInfoErrorCode implements ErrorCode {
    
    GENRE_NOT_FOUND(40404, "존재하지 않는 장르입니다.", HttpStatus.NOT_FOUND),
    COUNTRY_NOT_FOUND(40406, "존재하지 않는 나라입니다.", HttpStatus.NOT_FOUND),
    ACTOR_NOT_FOUND(40407, "존재하지 않는 배우입니다.", HttpStatus.NOT_FOUND),
    DIRECTOR_NOT_FOUND(40408, "존재하지 않는 감독입니다.", HttpStatus.NOT_FOUND),
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
